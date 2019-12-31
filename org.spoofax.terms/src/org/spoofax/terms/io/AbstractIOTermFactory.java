/*
 * Created on 27. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.interpreter.terms.io.IFileTermFactory;
import org.spoofax.terms.AbstractTermFactory;
import org.spoofax.terms.ParseError;
import org.spoofax.terms.io.binary.TermReader;

@Deprecated
public abstract class AbstractIOTermFactory extends AbstractTermFactory implements IFileTermFactory {

	private final TAFTermReader reader = new TermReader(this);
	
	public AbstractIOTermFactory() {
		super();
	}

    public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException, ParseError {
        return reader.parseFromStream(inputStream);
    }
    
    public IStrategoTerm parseFromFile(String path) throws IOException, ParseError {
    	return reader.parseFromFile(path);
    }

    public void unparseToFile(IStrategoTerm t, OutputStream ous) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(ous));
        unparseToFile(t, out);
    }

    public void unparseToFile(IStrategoTerm t, Writer out) throws IOException {
        ITermPrinter tp = new InlineWriter(out);
        t.prettyPrint(tp);
    }

}
