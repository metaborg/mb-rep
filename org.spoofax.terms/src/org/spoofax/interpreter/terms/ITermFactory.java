/*
 * Created on 30. aug.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.interpreter.terms;

import org.spoofax.terms.ParseError;
import org.spoofax.terms.StringTermReader;
import org.spoofax.terms.io.TAFTermReader;

/**
 * 
 * @author Karl T. Kalleberg <karltk add strategoxt.org>
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface ITermFactory extends IStrategoTermBuilder {
    
	/**
	 * Parses a term from a string.
	 * 
	 * @see StringTermReader#parseFromString(java.lang.String)
	 * @see TAFTermReader#parseFromStream(java.io.InputStream)
	 * @see TAFTermReader#parseFromFile(String)
	 */
	public IStrategoTerm parseFromString(String text) throws ParseError;

    public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoAppl old);
    
    public IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old);
    
    public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old);
    
    public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old);
    
    public ITermFactory getFactoryWithStorageType(int storageType);
}
