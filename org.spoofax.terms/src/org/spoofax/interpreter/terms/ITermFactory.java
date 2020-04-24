/*
 * Created on 30. aug.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

import org.spoofax.terms.ParseError;
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
     * @see TAFTermReader#parseFromString(String)
     * @see TAFTermReader#parseFromStream(java.io.InputStream)
     * @see TAFTermReader#parseFromFile(String)
     */
    IStrategoTerm parseFromString(String text) throws ParseError;

    IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoAppl old);

    IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old);

    IStrategoList replaceListCons(IStrategoTerm head, IStrategoList tail, IStrategoTerm oldHead, IStrategoList oldTail);

    IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old);

    IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old);
}
