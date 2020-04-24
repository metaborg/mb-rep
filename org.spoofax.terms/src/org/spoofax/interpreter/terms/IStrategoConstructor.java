/*
 * Created on 30. aug.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

/**
 * A Stratego term constructor.
 */
public interface IStrategoConstructor extends IStrategoTerm {

    /**
     * Gets the name of the constructor.
     *
     * The empty string is reserved for use with tuples.
     *
     * @return the name of the constructor
     */
    String getName();

    /**
     * Gets the arity of the constructor.
     *
     * @return the arity of the constructor
     */
    int getArity();

    @Deprecated
    IStrategoAppl instantiate(ITermFactory factory, IStrategoTerm... kids);
    @Deprecated
    IStrategoAppl instantiate(ITermFactory factory, IStrategoList kids);

}
