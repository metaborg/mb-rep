package org.spoofax.interpreter.adapter.aterm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.spoofax.interpreter.terms.BasicTermFactory;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.TermConverter;

import aterm.pure.BAFReader;

/**
 * A basic term factory that can read BAF aterms.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class BAFBasicTermFactory extends BasicTermFactory {
    
    private final WrappedATermFactory wrappedFactory = new UnsharedWrappedATermFactory();
    
    private final TermConverter converter = new TermConverter(this);

    @Override
    public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException {
        PushbackInputStream bis = new PushbackInputStream(inputStream, BAFReader.BAF_MAGIC_SIZE);
        
        return parseFromStream(bis);
    }  
    
    @Override
    public IStrategoTerm parseFromStream(PushbackInputStream inputStream)
            throws IOException {
        
        if (BAFReader.isBinaryATerm(inputStream)) {
            IStrategoTerm result = wrappedFactory.parseFromStream(inputStream);
            return converter.convert(result);
        } else {
            return super.parseFromStream(inputStream);
        }
    }
}
