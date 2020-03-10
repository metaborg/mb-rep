/*
 * Created on 30. aug.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;


import java.util.NoSuchElementException;


/**
 * A Stratego List term.
 */
public interface IStrategoList extends IStrategoTerm {
	// Too restrictive for client classes: Iterable<IStrategoTerm>

    // FIXME: Should be named getSize() for Kotlin compatibility
    /**
     * Gets the size of the list.
     *
     * @return the size of the list
     */
    int size();

    // FIXME: Should be named getHead() for Kotlin compatibility
    /**
     * Gets the head of the list.
     *
     * @return the head of the list
     * @throws NoSuchElementException The list is empty.
     */
    IStrategoTerm head();

    // FIXME: Should be named getTail() for Kotlin compatibility
    /**
     * Gets the tail of the list.
     *
     * @return the tail of the list
     * @throws IllegalStateException The list is empty.
     */
    IStrategoList tail();

    /**
     * Gets whether the list is empty.
     *
     * @return {@code true} when the list is empty; otherwise, {@code false}.
     */
    boolean isEmpty();

    /** @deprecated Use {@link IStrategoTermBuilder#makeListCons(IStrategoTerm, IStrategoList)} instead. */
    @Deprecated
    IStrategoList prepend(IStrategoTerm prefix);

    /** @deprecated Use {@link #getSubterm(int)} instead. */
    @Deprecated // useless; only causes incompatibility with other base classes
    IStrategoTerm get(int index);

    /**
     * A Builder for lists that may build a more efficiently represent the list internally than head-tail
     */
    interface Builder {
        void add(IStrategoTerm term);
        IStrategoList build();
        boolean isEmpty();
    }

}
