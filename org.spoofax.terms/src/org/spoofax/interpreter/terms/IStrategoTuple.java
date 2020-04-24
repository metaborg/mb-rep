/*
 * Created on 17. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

// TODO: A Tuple is nothing more than an Appl with an empty string as the constructor.
// Therefore, this interface and the distinction should be removed.
/**
 * A Stratego Tuple term.
 */
public interface IStrategoTuple extends IStrategoTerm {

    // FIXME: Should be named getSize() for Kotlin compatibility
    /**
     * Gets the size of the tuple.
     *
     * @return the size of the tuple.
     */
    int size();

    /**
     * Gets the element of the tuple with the specified index.
     *
     * @param index the index to look for
     * @return the term at the specified index
     * @throws IndexOutOfBoundsException the index is out of bounds
     */
    IStrategoTerm get(int index);

}
