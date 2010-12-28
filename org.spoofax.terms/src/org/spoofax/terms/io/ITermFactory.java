package org.spoofax.terms.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface ITermFactory extends org.spoofax.terms.INonIOTermFactory {

    public IStrategoTerm parseFromFile(String path) throws IOException;
    
    public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException;
    
    public void unparseToFile(IStrategoTerm t, OutputStream ous) throws IOException;
    
    public void unparseToFile(IStrategoTerm t, Writer out) throws IOException;

}
