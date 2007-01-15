/*
 * Created on 13.aug.2005
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk@ii.uib.no>
 * 
 * Licensed under the GNU General Public License, v2
 */
package aterm.pure;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermList;
import aterm.ParseError;

public class BAFReader {

    private static final int BAF_MAGIC = 0xBAF;

    private static final int BAF_VERSION = 0x300;

    private static final int HEADER_BITS = 32;

    private BitStream reader;

    private int nrUniqueSymbols = -1;

    private SymEntry[] symbols;

    private PureFactory factory;

    private static boolean isDebugging = false;

    class SymEntry {

        public AFun fun;

        public int arity;

        public int nrTerms;

        public int termWidth;

        public ATerm[] terms;

        public int[] nrTopSyms;

        public int[] symWidth;

        public int[][] topSyms;

    }

    public BAFReader(PureFactory factory, InputStream inputStream) {
        this.factory = factory;
        reader = new BitStream(inputStream);
    }

    public ATerm readFromBinaryFile(boolean headerAlreadyRead) throws ParseError, IOException {

        if(!headerAlreadyRead && !isBinaryATerm(reader))
            throw new ParseError("Input is not a BAF file");

        int val = reader.readInt();

        if (val != BAF_VERSION)
            throw new ParseError("Wrong BAF version (wanted " + BAF_VERSION + ", got " + val + "), giving up");

        nrUniqueSymbols = reader.readInt();
        int nrUniqueTerms = reader.readInt();

        if(isDebugging) {
            debug("" + nrUniqueSymbols + " unique symbols");
            debug("" + nrUniqueTerms + " unique terms");
        }

        symbols = new SymEntry[nrUniqueSymbols];

        readAllSymbols();

        int i = reader.readInt();

        return readTerm(symbols[i]);
    }

    public static boolean isBinaryATerm(BufferedInputStream in) throws IOException {
        in.mark(10);
        if(isBinaryATerm(new BitStream(in)))
            return true;
        in.reset();
        return false;
    }

    private static boolean isBinaryATerm(BitStream in) throws IOException {
        try {
            int w0 = in.readInt();
            int w1 = in.readInt();

            if (w0 == 0 && w1 == BAF_MAGIC)
                return true;
        } catch(EOFException e) {}

        return false;
    }


    private void debug(String s) {
        System.err.println(s);
    }

    private ATerm readTerm(SymEntry e) throws ParseError, IOException {
        int arity = e.arity;
        ATerm[] args = new ATerm[arity];

        if(isDebugging)
            debug("readTerm() - " + e.fun.getName() + "[" + arity + "]");

        for (int i = 0; i < arity; i++) {
            int val = reader.readBits(e.symWidth[i]);
            if(isDebugging) {
                debug(" [" + i + "] - " + val);
                debug(" [" + i + "] - " + e.topSyms[i].length);
            }
            SymEntry argSym = symbols[e.topSyms[i][val]];

            val = reader.readBits(argSym.termWidth);
            if (argSym.terms[val] == null) {
                if(isDebugging )
                    debug(" [" + i + "] - recurse");
                argSym.terms[val] = readTerm(argSym);
            }

            if (argSym.terms[val] == null)
                throw new ParseError("Cannot be null");

            args[i] = argSym.terms[val];
        }

        /*
        switch (e.fun.getType()) {
        case ATerm.BLOB:
            reader.flushBitsFromReader();
            String t = reader.readString();
            return factory.makeBlob(t.getBytes());
        case ATerm.PLACEHOLDER:
            return factory.makePlaceholder(args[0]);
        }
        */

        if (e.fun.getName().equals("<int>")) {
            int val = reader.readBits(HEADER_BITS);
            return factory.makeInt(val);
        }
        if (e.fun.getName().equals("<real>")) {
            reader.flushBitsFromReader();
            String s = reader.readString();
            return factory.makeReal(new Double(s).doubleValue());
        }
        if (e.fun.getName().equals("[_,_]")) {
            if(isDebugging) {
                debug("--");
                for (int i = 0; i < args.length; i++)
                    debug(" + " + args[i].getClass());
            }
            return ((ATermList) args[1]).insert(args[0]);
        }
        if (e.fun.getName().equals("[]"))
            return factory.makeList();

        // FIXME: Add annotation case
        // FIXME: Add blob case
        // FIXME: Add placeholder case

        if(isDebugging) {
            debug(e.fun + " / " + args);
            for (int i = 0; i < args.length; i++)
                debug("" + args[i]);
        }
        return factory.makeAppl(e.fun, args);
    }

    private void readAllSymbols() throws IOException {

        for (int i = 0; i < nrUniqueSymbols; i++) {
            SymEntry e = new SymEntry();
            symbols[i] = e;

            AFun fun = readSymbol();
            e.fun = fun;
            int arity = e.arity = fun.getArity();

            int v = reader.readInt();
            e.nrTerms = v;
            e.termWidth = bitWidth(v);
            // FIXME: original code is inconsistent at this point!
            e.terms = (v == 0) ? null : new ATerm[v];

            if (arity == 0) {
                e.nrTopSyms = null;
                e.symWidth = null;
                e.topSyms = null;
            } else {

                e.nrTopSyms = new int[arity];
                e.symWidth = new int[arity];
                e.topSyms = new int[arity][];
            }
            for (int j = 0; j < arity; j++) {
                v = reader.readInt();
                e.nrTopSyms[j] = v;
                e.symWidth[j] = bitWidth(v);
                e.topSyms[j] = new int[v];

                for (int k = 0; k < e.nrTopSyms[j]; k++) {
                    v = reader.readInt();
                    e.topSyms[j][k] = v;
                }
            }
        }
    }

    private int bitWidth(int v) {
        int nrBits = 0;

        if (v <= 1)
            return 0;

        while (v != 0) {
            v >>= 1;
            nrBits++;
        }

        return nrBits;
    }

    private AFun readSymbol() throws IOException {
        String s = reader.readString();
        int arity = reader.readInt();
        int quoted = reader.readInt();

        if(isDebugging)
            debug(s + " / " + arity + " / " + quoted);

        return factory.makeAFun(s, arity, quoted != 0);
    }

    public static class BitStream {

        InputStream stream;
        private int bitsInBuffer;
        private int bitBuffer;

        public BitStream(InputStream inputStream) {
            stream = inputStream;
        }

        public int readInt() throws IOException {
            int[] buf = new int[5];

            buf[0] = readByte();
            
            // Check if 1st character is enough
            if((buf[0] & 0x80) == 0)
                return buf[0];

            buf[1]  = readByte();
            
            // Check if 2nd character is enough
            if((buf[0] & 0x40) == 0)
                return buf[1] + ((buf[0] & ~0xc0) << 8);

            buf[2] = readByte();

            // Check if 3rd character is enough
            if((buf[0] & 0x20) == 0 )
                return buf[2] + (buf[1] << 8) + ((buf[0] & ~0xe0) << 16);

            buf[3] = readByte();
            
            // Check if 4th character is enough
            if((buf[0] & 0x10) == 0 )
                return buf[3] + (buf[2] << 8) + (buf[1] << 16) +
                  ((buf[0] & ~0xf0) << 24);
            
            buf[4] = readByte();

            return buf[4] + (buf[3] << 8) + (buf[2] << 16) + (buf[1] << 24);
        }

        private int readByte() throws IOException {
            int c = stream.read();
            if(c == -1)
                throw new EOFException();
            return c;
        }

        public String readString() throws IOException {
            int l = readInt();
            byte[] b = new byte[l];
            stream.read(b, 0, b.length);
            return new String(b);
        }

        public int readBits(int nrBits) throws IOException {
            int mask = 1;
            int val = 0;
            
            for (int i=0; i<nrBits; i++) {
              if (bitsInBuffer == 0) {
                int v = readByte();
                if (v == -1)
                  return -1;
                bitBuffer = v;
                bitsInBuffer = 8;
              }
              val |= (((bitBuffer & 0x80) != 0) ? mask : 0);
              mask <<= 1;
              bitBuffer <<= 1;
              bitsInBuffer--;
            }
            
            return val;
        }

        public void flushBitsFromReader() {
            bitsInBuffer = 0;
        }
    }
}
