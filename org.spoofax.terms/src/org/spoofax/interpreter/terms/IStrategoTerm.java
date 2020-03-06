/*
 * Created on 30. aug.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;

/**
 * A Stratego annotated term.
 */
public interface IStrategoTerm extends ISimpleTerm, Serializable, Iterable<IStrategoTerm> {

    /** A Constructor Application term type. */
    int APPL = 1;
    /** A List term type. */
    int LIST = 2;
    /** An Int term type. */
    int INT = 3;
    /** A Real term type. */
    int REAL = 4;
    /** A String term type. */
    int STRING = 5;
    /** A Term Constructor term type. */
    int CTOR = 6;
    /** A Tuple term type. */
    int TUPLE = 7;
    /** A Ref term type. */
    int REF = 8;
    /** A Blob term type. */
    int BLOB = 9;
    /** A Placeholder term type. */
    int PLACEHOLDER = 10;

    /** @deprecated Use {@link Integer#MAX_VALUE} instead. */
    @Deprecated int INFINITE = Integer.MAX_VALUE;

    @Override int getSubtermCount();

    @Override IStrategoTerm getSubterm(int index);

    /**
     * Gets an array with all subterms of this term.
     *
     * Do not change the elements in the returned array.
     *
     * @return an array with all subterms;
     * or an empty array when the term does not support subterms
     */
    IStrategoTerm[] getAllSubterms();

    /**
     * Gets the type of term.
     *
     * @return an integer value, one of: {@link #APPL}, {@link #LIST}, {@link #INT}, {@link #REAL},
     * {@link #STRING}, {@link #CTOR}, {@link #TUPLE}, {@link #REF}, or {@link #BLOB}.
     */
    int getTermType();

    /**
     * Gets the annnotations on this term.
     *
     * @return a List term with the annotations; or an empty List term when there are none
     */
    IStrategoList getAnnotations();

    /**
     * Performs a match of this tree against the specified tree.
     *
     * Two trees match if they have the same structure and annotations,
     * irrespective of the term factory used to create them.
     *
     * @param second the other tree to match
     * @return {@code true} when this tree matches the specified tree;
     * otherwise, {@code false}
     */
    boolean match(IStrategoTerm second);
    
    /** @deprecated Use {@link #writeAsString(Appendable, int)} instead. */
    @Deprecated
    void prettyPrint(ITermPrinter pp);

    /**
     * Creates a string representation of this term up to the specified depth.
     *
     * Use this representation only for debugging. To write a term to disk,
     * use an {@link ITermPrinter} implementation.
     *
     * @param maxDepth how many levels of the tree to write, or {@link Integer#MAX_VALUE} for infinitely many
     * @return the created string representation
     */
    String toString(int maxDepth);

    /**
     * Write a term to some output, such as a {@link Writer}.
     *
     * @param output the {@link Appendable} to write to
     */
    default void writeAsString(Appendable output) throws IOException {
        writeAsString(output, Integer.MAX_VALUE);
    }

    /**
     * Write a term to some output, such as a {@link Writer}.
     *
     * @param output the {@link Appendable} to write to
     * @param maxDepth how many levels of the tree to write, or {@link Integer#MAX_VALUE} for infinitely many
     */
    void writeAsString(Appendable output, int maxDepth) throws IOException;

    // TODO: Remove Iterable<IStrategoTerm>
    /** @deprecated Use {@link #getAllSubterms()} instead. */
    @Override @Deprecated
    Iterator<IStrategoTerm> iterator();

}
