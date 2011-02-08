package org.spoofax.terms.io.binary;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.io.TAFTermReader;

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
	public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException, ParseError {
        /*
    	BufferedInputStream bis;
        if (inputStream instanceof BufferedInputStream) {
            bis = (BufferedInputStream) inputStream;
        } else if (inputStream instanceof FileInputStream) {
        	FileChannel channel = ((FileInputStream) inputStream).getChannel();
			ChannelPushbackInputStream pis = new ChannelPushbackInputStream(channel);
			if (BAFReader.isBinaryATerm(pis)) {
				return new BAFReader(this, pis).readFromBinaryFile(true);
			} else {
				return super.parseFromStream(pis);
			}
        } else {
            bis = new BufferedInputStream(inputStream);
        }
        if (BAFReader.isBinaryATerm(bis)) {
            return new BAFReader(this, bis).readFromBinaryFile(true);
        } else {
            return super.parseFromStream(bis);
        }
        */
    	BufferedInputStream bis = null;
    	try {
	        if (inputStream instanceof BufferedInputStream)
	            bis = (BufferedInputStream) inputStream;
	        else
	            bis = new BufferedInputStream(inputStream);
	        
	        if (BAFReader.isBinaryATerm(bis)) {
	            return new BAFReader(getFactory(), bis).readFromBinaryFile(true);
	        } else if (SAFReader.isStreamingATerm(bis)) {
	        	return SAFReader.readTermFromSAFStream(factory, bis);
	        } else {
	            return super.parseFromStream(bis);
	        }
    	} finally {
    		if (bis != null) bis.close();
    	}
    }

}
