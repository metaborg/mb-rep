package org.spoofax.interpreter.adapter.aterm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.spoofax.interpreter.terms.BasicTermFactory;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.TermConverter;

import aterm.ATerm;
import aterm.pure.BAFReader;

/**
 * A basic term factory that can read BAF aterms.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class BAFBasicTermFactory extends BasicTermFactory {
    
    private final TermConverter converter = new TermConverter(this);
    
    private final WrappedATermFactory wrappedFactory;
    
    public BAFBasicTermFactory() {
        this(new UnsharedWrappedATermFactory());
    }
    
    public BAFBasicTermFactory(WrappedATermFactory wrappedFactory) {
        this.wrappedFactory = wrappedFactory;
    }

    @Override
    public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException {
        BufferedInputStream bis;
        if (inputStream instanceof BufferedInputStream)
            bis = (BufferedInputStream) inputStream;
        else
            bis = new BufferedInputStream(inputStream);
        
        if (BAFReader.isBinaryATerm(bis)) {
            ATerm result = new BAFReader(wrappedFactory.getFactory(), bis).readFromBinaryFile(true);
            return converter.convert(wrappedFactory.wrapTerm(result));
        } else {
            return super.parseFromStream(bis);
        }
    }
}
