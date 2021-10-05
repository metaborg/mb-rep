package org.spoofax.terms.io.binary;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.nio.charset.Charset;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.io.TAFTermReader;
import org.spoofax.terms.util.IOStreamExt;

/**
 * A term reader that supports both textual and binary ATerms.
 *
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class TermReader extends TAFTermReader {

	public TermReader(ITermFactory factory) {
		super(factory);
	}

    @Override
    public IStrategoTerm read(InputStream inputStream, Charset characterSet) throws IOException {
        final BufferedInputStream bis = IOStreamExt.ensureBuffered(inputStream);

        if (BAFReader.isBinaryATerm(bis)) {
            return new BAFReader(factory, bis).readFromBinaryFile(true);
        } else if (SAFReader.isStreamingATerm(bis)) {
            return SAFReader.readTermFromSAFStream(factory, bis);
        } else {
            final PushbackReader pr = new PushbackReader(new InputStreamReader(bis, characterSet));
            return super.parseFromStream(pr);
        }
        // Do not close the reader, doing so will also close the backing stream.
    }

    @Override
	public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException, ParseError {
        try {
            return read(inputStream);
        } finally {
            inputStream.close();
        }
    }

}
