package org.spoofax.terms.io.baf;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.io.StreamTermReader;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class BAFStreamTermReader extends StreamTermReader {
	
	public BAFStreamTermReader(ITermFactory factory) {
		super(factory);
	}
    
    public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException {
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
    	BufferedInputStream bis;
        if (inputStream instanceof BufferedInputStream)
            bis = (BufferedInputStream) inputStream;
        else
            bis = new BufferedInputStream(inputStream);
        
        if (BAFReader.isBinaryATerm(bis)) {
            return new BAFReader(getFactory(), bis).readFromBinaryFile(true);
        } else {
            return super.parseFromStream(bis);
        }
    }

}
