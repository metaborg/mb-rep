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

public interface IStrategoTerm extends ISimpleTerm, Serializable{

    public static final int APPL = 1;
    public static final int LIST = 2;
    public static final int INT = 3;
    public static final int REAL = 4;
    public static final int STRING = 5;
    public static final int CTOR = 6;
    public static final int TUPLE = 7;
    public static final int REF = 8;
    public static final int BLOB = 9;
    public static final int PLACEHOLDER = 10;
    
    public static final int MUTABLE = 0;
    public static final int IMMUTABLE = 1;
    public static final int SHARABLE = 2;
    public static final int MAXIMALLY_SHARED = 3;
    
    public static final int INFINITE = Integer.MAX_VALUE;

    public int getSubtermCount();
    public IStrategoTerm getSubterm(int index);
    public IStrategoTerm[] getAllSubterms();
    
    public int getTermType();
    
    /**
     * Indicates the assumptions that can be made about how this term is stored.
     * 
     * One of {@value #MUTABLE}, {@value #IMMUTABLE}, {@value #SHARABLE} and
     * {@value #MAXIMALLY_SHARED}. For each a specific contract exists; 
     * only {@value #MUTABLE} poses no restrictions on the implementation.
     * 
     * All non-{@value #MUTABLE} terms are expected to have an O(1)
     * {@link #hashCode()} implementation.
     * 
     * A general contract is that the storage type of a term must always 
     * be smaller than or equal to the storage types of its subterms.
     * 
     * Finally, when multiple term factories are used together,
     * they should use the same hash codes for equal terms: 
     * the TermFactory classes should be used as a reference.
     */
    public int getStorageType();
    
    public IStrategoList getAnnotations();
    
    public boolean match(IStrategoTerm second);
    
    /**
     * @see org.spoofax.terms.io.TAFTermReader#unparseToFile(IStrategoTerm, java.io.OutputStream)
     * @see org.spoofax.terms.io.TAFTermReader#unparseToFile(IStrategoTerm, java.io.Writer)
     * @see #writeAsString(Appendable, int)
     */
    @Deprecated
    public void prettyPrint(ITermPrinter pp);
    
    public String toString(int maxDepth);
    
    /**
     * Write a term to some output, such as a {@link Writer}.
     * 
     * @param maxDepth How many levels of the tree to write, or {@link #INFINITE} for infinitely many.
     */
    public void writeAsString(Appendable output, int maxDepth) throws IOException;
}
