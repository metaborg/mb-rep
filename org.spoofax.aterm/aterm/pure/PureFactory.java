/*
 * Java version of the ATerm library
 * Copyright (C) 2002, CWI, LORIA-INRIA
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package aterm.pure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import shared.SharedObjectFactory;
import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermBlob;
import aterm.ATermFactory;
import aterm.ATermInt;
import aterm.ATermList;
import aterm.ATermPlaceholder;
import aterm.ATermReal;
import aterm.ParseError;

public class PureFactory extends SharedObjectFactory implements ATermFactory {

	private static int DEFAULT_TERM_TABLE_SIZE = 16; // means 2^16 entries

	private ATermListImpl protoList;
	private ATermApplImpl protoAppl;
	private ATermIntImpl protoInt;
	private ATermRealImpl protoReal;
	private ATermBlobImpl protoBlob;
	private ATermPlaceholderImpl protoPlaceholder;
	private AFunImpl protoAFun;

	private ATermList empty;

	static public int abbrevSize(int abbrev) {
		int size = 1;

		if (abbrev == 0) {
			return 2;
		}

		while (abbrev > 0) {
			size++;
			abbrev /= 64;
		}

		return size;
	}

	public PureFactory() {
		this(DEFAULT_TERM_TABLE_SIZE);
	}

	public PureFactory(int termTableSize) {
		super(termTableSize);

		protoList = new ATermListImpl(this);
		protoAppl = new ATermApplImpl(this);
		protoInt = new ATermIntImpl(this);
		protoReal = new ATermRealImpl(this);
		protoBlob = new ATermBlobImpl(this);
		protoPlaceholder = new ATermPlaceholderImpl(this);
		protoAFun = new AFunImpl(this);

		protoList.init(42, null, null, null);
		empty = (ATermList) build(protoList);
		((ATermListImpl) empty).init(42, empty, null, null);
	}

	public ATermInt makeInt(int val) {
		return makeInt(val, empty);
	}

	public ATermReal makeReal(double val) {
		return makeReal(val, empty);
	}

	public ATermList makeList() {
		return empty;
	}

	public ATermList makeList(ATerm singleton) {
		return makeList(singleton, empty, empty);
	}

	public ATermList makeList(ATerm first, ATermList next) {
		return makeList(first, next, empty);
	}

	public ATermPlaceholder makePlaceholder(ATerm type) {
		return makePlaceholder(type, empty);
	}

	public ATermBlob makeBlob(byte[] data) {
		return makeBlob(data, empty);
	}

	public AFun makeAFun(String name, int arity, boolean isQuoted) {
		synchronized (protoAFun) {
			protoAFun.initHashCode(name, arity, isQuoted);
			return (AFun) build(protoAFun);
		}
	}

	public ATermInt makeInt(int value, ATermList annos) {
		synchronized (protoInt) {
			protoInt.initHashCode(annos, value);
			return (ATermInt) build(protoInt);
		}
	}

	public ATermReal makeReal(double value, ATermList annos) {
		synchronized (protoReal) {
			protoReal.init(hashReal(annos, value), annos, value);
			return (ATermReal) build(protoReal);
		}
	}

	static private int hashReal(ATermList annos, double value) {
		return shared.HashFunctions.doobs(new Object[] { annos, new Double(value)});
	}

	public ATermPlaceholder makePlaceholder(ATerm type, ATermList annos) {
		synchronized (protoPlaceholder) {
			protoPlaceholder.init(hashPlaceholder(annos, type), annos, type);
			return (ATermPlaceholder) build(protoPlaceholder);
		}
	}

	static private int hashPlaceholder(ATermList annos, ATerm type) {
		return shared.HashFunctions.doobs(new Object[] { annos, type });
	}

	public ATermBlob makeBlob(byte[] data, ATermList annos) {
		synchronized (protoBlob) {
			protoBlob.init(hashBlob(annos, data), annos, data);
			return (ATermBlob) build(protoBlob);
		}
	}

	static private int hashBlob(ATermList annos, byte[] data) {
		return shared.HashFunctions.doobs(new Object[] { annos, data });
	}

	public ATermList makeList(ATerm first, ATermList next, ATermList annos) {
		synchronized (protoList) {
			protoList.initHashCode(annos, first, next);
			return (ATermList) build(protoList);
		}
	}

	private static ATerm[] array0 = new ATerm[0];

	public ATermAppl makeAppl(AFun fun, ATerm[] args) {
		return makeAppl(fun, args, empty);
	}

	public ATermAppl makeAppl(AFun fun, ATerm[] args, ATermList annos) {
		synchronized (protoAppl) {
			protoAppl.initHashCode(annos, fun, args);
			return (ATermAppl) build(protoAppl);
		}
	}

	public ATermAppl makeApplList(AFun fun, ATermList list) {
		return makeApplList(fun, list, empty);
	}

	public ATermAppl makeApplList(AFun fun, ATermList list, ATermList annos) {
		ATerm[] arg_array;

		arg_array = new ATerm[list.getLength()];

		int i = 0;
		while (!list.isEmpty()) {
			arg_array[i++] = list.getFirst();
			list = list.getNext();
		}
		return makeAppl(fun, arg_array, annos);
	}

	public ATermAppl makeAppl(AFun fun) {
		return makeAppl(fun, array0);
	}

	public ATermAppl makeAppl(AFun fun, ATerm arg) {
		ATerm[] argarray1 = new ATerm[] { arg };
		return makeAppl(fun, argarray1);
	}

	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2) {
		ATerm[] argarray2 = new ATerm[] { arg1, arg2 };
		return makeAppl(fun, argarray2);
	}

	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3) {
		ATerm[] argarray3 = new ATerm[] { arg1, arg2, arg3 };
		return makeAppl(fun, argarray3);
	}

	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3, ATerm arg4) {
		ATerm[] argarray4 = new ATerm[] { arg1, arg2, arg3, arg4 };
		return makeAppl(fun, argarray4);
	}

	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3, ATerm arg4, ATerm arg5) {
		ATerm[] argarray5 = new ATerm[] { arg1, arg2, arg3, arg4, arg5 };
		return makeAppl(fun, argarray5);
	}

	public ATermAppl makeAppl(AFun fun, ATerm arg1, ATerm arg2, ATerm arg3, ATerm arg4, ATerm arg5, ATerm arg6) {
		ATerm[] args = { arg1, arg2, arg3, arg4, arg5, arg6 };
		return makeAppl(fun, args);
	}

	public ATermAppl makeAppl(
		AFun fun,
		ATerm arg1,
		ATerm arg2,
		ATerm arg3,
		ATerm arg4,
		ATerm arg5,
		ATerm arg6,
		ATerm arg7) {
		ATerm[] args = { arg1, arg2, arg3, arg4, arg5, arg6, arg7 };
		return makeAppl(fun, args);
	}

	public ATermList getEmpty() {
		return empty;
	}

	public ATerm make(String trm) {
		return parse(trm);
	}

	public ATerm make(String pattern, List args) {
		return make(parse(pattern), args);
	}

	public ATerm make(String pattern, Object arg1) {
		List args = new LinkedList();
		args.add(arg1);
		return make(pattern, args);
	}

	public ATerm make(String pattern, Object arg1, Object arg2) {
		List args = new LinkedList();
		args.add(arg1);
		args.add(arg2);
		return make(pattern, args);
	}

	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3) {
		List args = new LinkedList();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		return make(pattern, args);
	}

	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3, Object arg4) {
		List args = new LinkedList();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		return make(pattern, args);
	}

	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		List args = new LinkedList();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		return make(pattern, args);
	}

	public ATerm make(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		List args = new LinkedList();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		args.add(arg6);
		return make(pattern, args);
	}

	public ATerm make(
		String pattern,
		Object arg1,
		Object arg2,
		Object arg3,
		Object arg4,
		Object arg5,
		Object arg6,
		Object arg7) {
		List args = new LinkedList();
		args.add(arg1);
		args.add(arg2);
		args.add(arg3);
		args.add(arg4);
		args.add(arg5);
		args.add(arg6);
		args.add(arg7);
		return make(pattern, args);
	}

	public ATerm make(ATerm pattern, List args) {
		return pattern.make(args);
	}

	ATerm parsePattern(String pattern) throws ParseError {
		return parse(pattern);
	}

	protected boolean isDeepEqual(ATermImpl t1, ATerm t2) {
		throw new UnsupportedOperationException("not yet implemented!");
	}

	private ATerm readFromSharedTextFile(ATermReader reader) throws IOException {
        TAFReader r = new TAFReader(this);
        reader.initializeSharing();
        return r.parseFromReader(reader);
	}

	private ATerm readFromTextFile(ATermReader reader) throws IOException {
        TAFReader r = new TAFReader(this);
        return r.parseFromReader(reader);
	}

	public ATerm readFromTextFile(InputStream stream) throws IOException {
		ATermReader reader = new ATermReader(new InputStreamReader(stream));
		reader.readSkippingWS();

		ATerm t = readFromTextFile(reader);
        //Nick
        reader.close();
        return t;
    }

	public ATerm readFromSharedTextFile(InputStream stream) throws IOException {
		ATermReader reader = new ATermReader(new InputStreamReader(stream));
		reader.readSkippingWS();

		if (reader.getLastChar() != '!') {
			throw new IOException("not a shared text file!");
		}

		reader.readSkippingWS();

        //Nick
        ATerm t = readFromSharedTextFile(reader);
        reader.close();
        return t;
    }

	public ATerm readFromBinaryFile(InputStream stream) throws ParseError, IOException {
        return readFromBinaryFile(stream, false);
    }
    
    public ATerm readFromBinaryFile(InputStream stream, boolean headerRead) throws ParseError, IOException {
        BAFReader r = new BAFReader(this, stream);
        ATerm t = r.readFromBinaryFile(headerRead);
        r.close();
        return t;
    }

	public ATerm readFromFile(InputStream stream) throws IOException {
        
        boolean r = BAFReader.isBinaryATerm(stream);
        
        if(r)
            return readFromBinaryFile(stream, true);

		//Nick ATermReader reader = new ATermReader(new InputStreamReader(stream));
        ATermReader reader = new ATermReader(toBuffer((FileInputStream)stream));

        
		reader.readSkippingWS();

		int last_char = reader.getLastChar();
        ATerm t;
        if (last_char == '!') {
			reader.readSkippingWS();
            // Nick
            t = readFromSharedTextFile(reader);
		}
		else if (
			Character.isLetterOrDigit((char) last_char) || last_char == '_' || last_char == '[' || last_char == '-') {
			// Nick
            t = readFromTextFile(reader);
		}
		else {
            reader.close();
            throw new RuntimeException("BAF files are not supported by this factory.");
		}
        reader.close();
        return t;
    }

    private ByteBuffer toBuffer(FileInputStream fis) {
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        try {
            int sz = (int)fc.size();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
            // See also http://javaalmanac.com/egs/java.nio/ReadChannel.html?l=rel
            bb = bb.load();
            fc.close();

            return bb;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ATerm readFromFile(String file) throws IOException {
        return readFromFile(new FileInputStream(file));
    }

	public ATerm importTerm(ATerm term) {
		throw new RuntimeException("not yet implemented!");
	}

    public ATerm parse(String trm) {
        TAFReader r = new TAFReader(this);
        return r.parse(trm);
    }
}

class HashedWeakRef extends WeakReference {
	protected HashedWeakRef next;

	public HashedWeakRef(Object object, HashedWeakRef next) {
		super(object);
		this.next = next;
	}
}

class ATermReader {
	private static final int INITIAL_TABLE_SIZE = 2048;
	private static final int TABLE_INCREMENT = 4096;
	private Reader reader;
    private ByteBuffer buffer;
    private int last_char;
	private int pos;

	private int nr_terms;
	private ATerm[] table;

	public ATermReader(Reader reader) {
        assert false : "Do not use this!";
        this.reader = reader;
		last_char = -1;
		pos = 0;
	}

    public ATermReader(ByteBuffer buffer) {
        this.buffer = buffer;
        last_char = -1;
        pos = 0;
    }

	public void initializeSharing() {
		table = new ATerm[INITIAL_TABLE_SIZE];
		nr_terms = 0;
	}

	public void storeNextTerm(ATerm t, int size) {
		if (table == null) {
			return;
		}

		if (size <= PureFactory.abbrevSize(nr_terms)) {
			return;
		}

		if (nr_terms == table.length) {
			ATerm[] new_table = new ATerm[table.length + TABLE_INCREMENT];
			System.arraycopy(table, 0, new_table, 0, table.length);
			table = new_table;
		}

		table[nr_terms++] = t;
	}

	public ATerm getTerm(int index) {
		if (index < 0 || index >= nr_terms) {
			throw new RuntimeException("illegal index");
		}
		return table[index];
	}

    public int read() throws IOException {
        try {
            last_char = buffer.get();
            pos++;
            return last_char;
        }
        catch (RuntimeException e) {
            return -1;
        }
    }

    public int readSkippingWS() throws IOException {
        try {
            do {
                last_char = buffer.get();
                pos++;
            }
            while (isWhiteSpace((char) last_char));
            return last_char;
        }
        catch (RuntimeException e) {
            return -1;
        }
	}

    private static boolean isWhiteSpace(final char ch) {
        //Nick Character.isWhitespace(ch);
        return ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t';
    }

    public int skipWS() throws IOException {
        try {
            while (isWhiteSpace((char) last_char)) {
                last_char = buffer.get();
                pos++;
            }

            return last_char;
        }
        catch (RuntimeException e) {
            return -1;
        }
    }

	public int readOct() throws IOException {
		int val = Character.digit((char) last_char, 8);
		val += Character.digit((char) read(), 8);

		if (val < 0) {
			throw new ParseError("octal must have 3 octdigits.");
		}

		val += Character.digit((char) read(), 8);

		if (val < 0) {
			throw new ParseError("octal must have 3 octdigits");
		}

		return val;
	}

	public int getLastChar() {
		return last_char;
	}

	public int getPosition() {
		return pos;
	}

    // Nick
    public void close() {
        try {
            if(reader != null) {
                reader.close();
                reader = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(buffer != null) {
            buffer.clear();
        }
        if (table != null) {
            for (int i = 0; i < table.length; i++) {
                table[i] = null;
            }
            table = null;
        }
    }
}
