/*
 * Created on 15. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

/**
 * A Stratego constructor application term.
 */
public interface IStrategoAppl extends IStrategoTerm, IStrategoNamed {

    /**
     * Gets the constructor being applied.
     *
     * @return a term constructor
     */
    IStrategoConstructor getConstructor();

}
