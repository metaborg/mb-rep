/*
 * Copyright (c) 2002-2007, CWI and INRIA
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.spoofax.terms.io.binary;

import static org.spoofax.interpreter.terms.IStrategoTerm.APPL;
import static org.spoofax.interpreter.terms.IStrategoTerm.INT;
import static org.spoofax.interpreter.terms.IStrategoTerm.LIST;
import static org.spoofax.interpreter.terms.IStrategoTerm.REAL;
import static org.spoofax.interpreter.terms.IStrategoTerm.STRING;
import static org.spoofax.interpreter.terms.IStrategoTerm.TUPLE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

/**
 * Writes the given ATerm to a (streamable) binary format. Supply the
 * constructor of this class with a ATerm and keep calling the serialize method
 * until the finished() method returns true.<br />
 * <br />
 * For example (yes I know this code is crappy, but it's simple):<blockquote>
 * 
 * <pre>
 * ByteBuffer buffer = ByteBuffer.allocate(8192);
 * BinaryWriter bw = new BinaryWriter(aterm);
 * while (!bw.isFinished()) {
 *     buffer.clear();
 *     bw.serialize(buffer);
 *     while (buffer.hasRemaining())
 *         channel.write(buffer); // Write the chunk of data to a channel
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Arnold Lankamp
 * @author Nathan Bruning (ported to use IStrategoTerm)
 */
public class SAFWriter {
    private final static int ISSHAREDFLAG = 0x00000080;

    private final static int ANNOSFLAG = 0x00000010;

    private final static int ISFUNSHARED = 0x00000040;

    private final static int APPLQUOTED = 0x00000020;

    private final static int STACKSIZE = 256;

    private final static int MINIMUMFREESPACE = 10;

    private final Map<IStrategoTerm, Integer> sharedTerms;

    private int currentKey;

    private final Map<Object, Integer> applSignatures;

    private int sigKey;

    private ATermMapping[] stack;

    private int stackPosition;

    private IStrategoTerm currentTerm;

    private int indexInTerm;

    private byte[] tempNameWriteBuffer;

    private ByteBuffer currentBuffer;

    /**
     * Constructor.
     * 
     * @param root
     *            The ATerm that needs to be serialized.
     */
    public SAFWriter(IStrategoTerm root) {
        super();

        sharedTerms = new HashMap<IStrategoTerm, Integer>();
        currentKey = 0;
        applSignatures = new HashMap<Object, Integer>();
        sigKey = 0;

        stack = new ATermMapping[STACKSIZE];
        stackPosition = 0;

        ATermMapping tm = new ATermMapping();
        tm.term = root;

        stack[stackPosition] = tm;
        currentTerm = root;

        indexInTerm = 0;
        tempNameWriteBuffer = null;
    }

    /**
     * Serializes the term from the position where it left of the last time this
     * method was called. Note that the buffer will be flipped before returned.
     * 
     * @param buffer
     *            The buffer that will be filled with data.
     */
    public void serialize(ByteBuffer buffer) {
        currentBuffer = buffer;

        while (currentTerm != null) {
            if (buffer.remaining() < MINIMUMFREESPACE)
                break;

            Integer id = sharedTerms.get(currentTerm);
            if (id != null) {
                buffer.put((byte) ISSHAREDFLAG);
                writeInt(id.intValue());

                stackPosition--; // Pop the term from the stack, since it's
                // subtree is shared.
            } else {

                visit(currentTerm);

                if (currentTerm.getTermType() == IStrategoTerm.LIST)
                    stack[stackPosition].nextPartOfList = (IStrategoList) currentTerm; // <-
                // for
                // ATermList->next
                // optimizaton.

                // Don't add the term to the shared list until we are completely
                // done with it.
                if (indexInTerm == 0)
                    sharedTerms.put(currentTerm, new Integer(currentKey++));
                else
                    break;
            }

            currentTerm = getNextTerm();
        }

        buffer.flip();
    }

    /**
     * nathan: added
     */
    protected void visit(IStrategoTerm term) {

        switch (term.getTermType()) {
        	case APPL:
        		voidVisitAppl((IStrategoAppl) term);
        	case INT:
        		voidVisitInt((IStrategoInt) term);
        	case LIST:
        		voidVisitList((IStrategoList) term);
        	case REAL:
        		voidVisitReal((IStrategoReal) term);
        	case STRING:
        		voidVisitString((IStrategoString) term);
        	case TUPLE:
        		voidVisitTuple((IStrategoTuple) term);
        	default:
        		throw new RuntimeException("Could not serializate term of type "
                    + term.getClass().getName() + " to SAF format.");
        }

    }

    private void voidVisitTuple(IStrategoTuple term) {
        writeAppl(term, term.getSubtermCount(), "", false);
    }

    private void voidVisitString(IStrategoString term) {
        writeAppl(term, term.stringValue(), term.stringValue(), true);
    }

    /**
     * Checks if we are done serializing.
     * 
     * @return true when we are done serializing; false otherwise.
     */
    public boolean isFinished() {
        return (currentTerm == null);
    }

    /**
     * Finds the next term we are going to serialize, based on the current state
     * of the stack.
     * 
     * @return The next term we are going to serialize.
     */
    private IStrategoTerm getNextTerm() {
        IStrategoTerm next = null;

        // Make sure the stack remains large enough
        ensureStackCapacity();

        while (next == null && stackPosition > -1) {
            ATermMapping current = stack[stackPosition];
            IStrategoTerm term = current.term;

            if (term.getSubtermCount() > current.subTermIndex + 1) {
                if (term.getTermType() != IStrategoTerm.LIST) {
                    next = term.getSubterm(++current.subTermIndex);
                } else {
                    IStrategoList nextList = current.nextPartOfList;
                    next = nextList.head();
                    current.nextPartOfList = nextList.tail();

                    current.subTermIndex++;
                }

                ATermMapping child = new ATermMapping();
                child.term = next;
                stack[++stackPosition] = child;
            } else if (!current.annosDone && !term.getAnnotations().isEmpty()) {
                next = term.getAnnotations();

                ATermMapping annos = new ATermMapping();
                annos.term = next;
                stack[++stackPosition] = annos;

                current.annosDone = true;
            } else {
                stackPosition--;
            }
        }

        return next;
    }

    /**
     * Resizes the stack when needed. When we're running low on stack space the
     * capacity will be doubled.
     */
    private void ensureStackCapacity() {
        int stackSize = stack.length;
        if (stackPosition + 1 == stackSize) {
            ATermMapping[] newStack = new ATermMapping[(stackSize << 1)];
            System.arraycopy(stack, 0, newStack, 0, stack.length);
            stack = newStack;
        }
    }

    /**
     * Returns a header for the given term.
     * 
     * @param term
     *            The term we are requesting a header for.
     * @return The constructed header.
     */
    private byte getHeader(IStrategoTerm term) {
        byte header = (byte) ATermConstants.ATermTypeForTerm(term);
        if (!term.getAnnotations().isEmpty())
            header = (byte) (header | ANNOSFLAG);

        return header;
    }

    /**
     * Structure that holds information about the state of the contained term.
     * 
     * @author Arnold Lankamp
     */
    protected static class ATermMapping {
        public IStrategoTerm term;

        public int subTermIndex = -1;

        public boolean annosDone = false;

        public IStrategoList nextPartOfList = null; // This is for a ATermList
        // 'nextTerm' optimalization
        // only.
    }

    /**
     * Write appl or string or tuple.
     * 
     * @param term
     *            the term
     * @param fun
     *            the constructor key, can be a IStrategoConstructor (for
     *            applications), a IStrategoString (for strings) or an integer,
     *            for tuples
     * @param name the constructor name, or string value, or empty string for a tuple 
     * @param isString true if the term is a string
     */
    protected void writeAppl(IStrategoTerm term, Object fun, String name,
            boolean isString) {
        if (indexInTerm == 0) {
            byte header = getHeader(term);

            Integer key = applSignatures.get(fun);
            if (key == null) {

                if (isString)
                    header = (byte) (header | APPLQUOTED);
                currentBuffer.put(header);

                writeInt(term.getSubtermCount());

                byte[] nameBytes = name.getBytes();
                int length = nameBytes.length;
                writeInt(length);

                int endIndex = length;
                int remaining = currentBuffer.remaining();
                if (remaining < endIndex)
                    endIndex = remaining;

                currentBuffer.put(nameBytes, 0, endIndex);

                if (endIndex != length) {
                    indexInTerm = endIndex;
                    tempNameWriteBuffer = nameBytes;
                }

                applSignatures.put(fun, new Integer(sigKey++));
            } else {
                header = (byte) (header | ISFUNSHARED);
                currentBuffer.put(header);

                writeInt(key.intValue());
            }
        } else {
            int length = tempNameWriteBuffer.length;

            int endIndex = length;
            int remaining = currentBuffer.remaining();
            if ((indexInTerm + remaining) < endIndex)
                endIndex = (indexInTerm + remaining);

            currentBuffer.put(tempNameWriteBuffer, indexInTerm,
                    (endIndex - indexInTerm));
            indexInTerm = endIndex;

            if (indexInTerm == length) {
                indexInTerm = 0;
                tempNameWriteBuffer = null;
            }
        }
    }

    /**
     * Serializes the given appl. The function name of the appl can be
     * serialized in chunks.
     * 
     */
    public void voidVisitAppl(IStrategoAppl arg) {
        writeAppl(arg, arg.getConstructor(), arg.getConstructor().getName(), false);
    }

    /**
     * Serializes the given int. Ints will always be serialized in one piece.
     */
    public void voidVisitInt(IStrategoInt arg) {
        currentBuffer.put(getHeader(arg));

        writeInt(arg.intValue());
    }

    /**
     * Serializes the given list. List information will always be serialized in
     * one piece.
     * 
     */
    public void voidVisitList(IStrategoList arg) {
        byte header = getHeader(arg);
        currentBuffer.put(header);

        writeInt(arg.size());
    }

    /**
     * Serializes the given real. Reals will always be serialized in one peice.
     */
    public void voidVisitReal(IStrategoReal arg) {
        currentBuffer.put(getHeader(arg));

        writeDouble(arg.realValue());
    }

    private final static int SEVENBITS = 0x0000007f;

    private final static int SIGNBIT = 0x00000080;

    private final static int LONGBITS = 8;

    /**
     * Splits the given integer in separate bytes and writes it to the buffer.
     * It will occupy the smallest amount of bytes possible. This is done in the
     * following way: the sign bit will be used to indicate that more bytes
     * coming, if this is set to 0 we know we are done. Since we are mostly
     * writing small values, this will save a considerable amount of space. On
     * the other hand a large number will occupy 5 bytes instead of the regular
     * 4.
     * 
     * @param value
     *            The integer that needs to be split and written.
     */
    private void writeInt(int value) {
        int intValue = value;

        if ((intValue & 0xffffff80) == 0) {
            currentBuffer.put((byte) (intValue & SEVENBITS));
            return;
        }
        currentBuffer.put((byte) ((intValue & SEVENBITS) | SIGNBIT));

        if ((intValue & 0xffffc000) == 0) {
            currentBuffer.put((byte) ((intValue >>> 7) & SEVENBITS));
            return;
        }
        currentBuffer.put((byte) (((intValue >>> 7) & SEVENBITS) | SIGNBIT));

        if ((intValue & 0xffe00000) == 0) {
            currentBuffer.put((byte) ((intValue >>> 14) & SEVENBITS));
            return;
        }
        currentBuffer.put((byte) (((intValue >>> 14) & SEVENBITS) | SIGNBIT));

        if ((intValue & 0xf0000000) == 0) {
            currentBuffer.put((byte) ((intValue >>> 21) & SEVENBITS));
            return;
        }
        currentBuffer.put((byte) (((intValue >>> 21) & SEVENBITS) | SIGNBIT));

        currentBuffer.put((byte) ((intValue >>> 28) & SEVENBITS));
    }

    /**
     * Splits the given double in separate bytes and writes it to the buffer.
     * Doubles will always occupy 8 bytes, since the convertion of a floating
     * point number to a long will always cause the high order bits to be
     * occupied.
     * 
     * @param value
     *            The integer that needs to be split and written.
     */
    private void writeDouble(double value) {
        long longValue = Double.doubleToLongBits(value);
        writeLong(longValue);
    }

    private void writeLong(long value) {
        for (int i = 0; i < LONGBITS; i++) {
            currentBuffer.put((byte) (value >>> (i * 8)));
        }
    }

    /**
     * Writes the given aterm to the given file. Blocks of 65536 bytes will be
     * used.
     * 
     * @param aTerm
     *            The ATerm that needs to be writen to file.
     * @param file
     *            The file to write to.
     * @throws IOException
     *             Thrown when an error occurs while writing to the given file.
     * 
     */
    public static void writeTermToSAFFile(IStrategoTerm aTerm, File file)
            throws IOException {
        SAFWriter binaryWriter = new SAFWriter(aTerm);

        ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
        ByteBuffer sizeBuffer = ByteBuffer.allocate(2);

        FileOutputStream fos = null;
        FileChannel fc = null;
        try {
            fos = new FileOutputStream(file);
            fc = fos.getChannel();

            byteBuffer.put((byte) '?');
            byteBuffer.flip();
            fc.write(byteBuffer);

            do {
                byteBuffer.clear();
                binaryWriter.serialize(byteBuffer);

                int blockSize = byteBuffer.limit();
                sizeBuffer.clear();
                sizeBuffer.put((byte) (blockSize & 0x000000ff));
                sizeBuffer.put((byte) ((blockSize >>> 8) & 0x000000ff));
                sizeBuffer.flip();

                fc.write(sizeBuffer);

                fc.write(byteBuffer);
            } while (!binaryWriter.isFinished());
        } finally {
            if (fc != null) {
                fc.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Writes the given aterm to a byte array. The reason this method returns a
     * byte array instead of a Java String is because otherwise the binary data
     * would be corrupted, since it would be automatically encoded in UTF-16
     * format (which would completely mess everything up). Blocks of 65536 bytes
     * will be used.
     * 
     * @param aTerm
     *            The ATerm that needs to be written to a byte array.
     * @return The serialized representation of the given ATerm contained in a
     *         byte array.
     * 
     */
    public static byte[] writeTermToSAFString(IStrategoTerm aTerm) {
        List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
        int totalBytesWritten = 0;

        SAFWriter binaryWriter = new SAFWriter(aTerm);
        do {
            ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
            binaryWriter.serialize(byteBuffer);

            buffers.add(byteBuffer);
            totalBytesWritten += byteBuffer.limit() + 2; // Increment by: buffer
            // size + 2 bytes
            // length spec.
        } while (!binaryWriter.isFinished());

        byte[] data = new byte[totalBytesWritten];
        int position = 0;
        int numberOfBuffers = buffers.size();
        for (int i = 0; i < numberOfBuffers; i++) {
            ByteBuffer buffer = buffers.get(i);
            int blockSize = buffer.limit();
            data[position++] = (byte) (blockSize & 0x000000ff);
            data[position++] = (byte) ((blockSize >>> 8) & 0x000000ff);

            System.arraycopy(buffer.array(), 0, data, position, blockSize);
            position += blockSize;
        }

        return data;
    }

    /**
     * Write to stream. Uses buffer of 65535 bytes.
     * 
     * @param term
     * @param out
     * @throws IOException
     */
    public static void writeTermToSAFStream(IStrategoTerm term, OutputStream out)
            throws IOException {

        SAFWriter binaryWriter = new SAFWriter(term);
        ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
        WritableByteChannel channel = Channels.newChannel(out);

        out.write((byte) '?');

        do {
            byteBuffer.clear();
            binaryWriter.serialize(byteBuffer);

            int blockSize = byteBuffer.limit();
            out.write((byte) (blockSize & 0x000000ff));
            out.write((byte) ((blockSize >>> 8) & 0x000000ff));

            channel.write(byteBuffer);

        } while (!binaryWriter.isFinished());

        // Do not close the channel, doing so will also close the backing
        // stream.

    }
}
