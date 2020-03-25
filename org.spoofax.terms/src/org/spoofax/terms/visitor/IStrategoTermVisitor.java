package org.spoofax.terms.visitor;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoRef;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

/**
 * Term visitor.
 */
public interface IStrategoTermVisitor {

    /**
     * Visits a constructor application term.
     *
     * @param term the term being visited
     * @return {@code true} when a top-down traversal should visit the subterms;
     * otherwise, {@code false}
     */
    boolean visit(IStrategoAppl term);

    /**
     * Visits a list term.
     *
     * @param term the term being visited
     * @return {@code true} when a top-down traversal should visit the subterms;
     * otherwise, {@code false}
     */
    boolean visit(IStrategoList term);

    /**
     * Visits a tuple term.
     *
     * @param term the term being visited
     * @return {@code true} when a top-down traversal should visit the subterms;
     * otherwise, {@code false}
     */
    boolean visit(IStrategoTuple term);

    /**
     * Visits an Int term.
     *
     * @param term the term being visited
     */
    void visit(IStrategoInt term);

    /**
     * Visits a Real term.
     *
     * @param term the term being visited
     */
    void visit(IStrategoReal term);

    /**
     * Visits a String term.
     *
     * @param term the term being visited
     */
    void visit(IStrategoString term);

    /**
     * Visits a Ref term.
     *
     * @param term the term being visited
     */
    void visit(IStrategoRef term);

    /**
     * Visits a placeholder term.
     *
     * @param term the term being visited
     * @return {@code true} when a top-down traversal should visit the subterms;
     * otherwise, {@code false}
     */
    boolean visit(IStrategoPlaceholder term);

    /**
     * Visits a term.
     *
     * @param term the term being visited
     * @return {@code true} when a top-down traversal should visit the subterms;
     * otherwise, {@code false}
     */
    boolean visit(IStrategoTerm term);
}
