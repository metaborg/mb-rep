package org.spoofax.interpreter.terms;

import org.spoofax.terms.StrategoList;

import java.util.NoSuchElementException;

/**
 * A Stratego Placeholder term.
 *
 * A placeholder indicates a place within a term where terms may be substituted or matched.
 * The template of a placeholder indicates the term being matched, deconstructed, or constructed.
 * They are most useful in the C implementation of ATerms, and are not really used in the Java
 * implementation.
 */
public interface IStrategoPlaceholder extends IStrategoTerm, IStrategoList {

    /**
     * Gets the template of the placeholder.
     *
     * @return the template
     */
    IStrategoTerm getTemplate();

    @Override
    default int size() { return 0; }

    @Override
    default IStrategoTerm head() { throw new NoSuchElementException(); }

    @Override
    default IStrategoList tail() { throw new IllegalStateException(); }

    @Override
    default boolean isEmpty() { return true; }

    @Override
    default IStrategoTerm get(int index) {
        throw new IndexOutOfBoundsException("List placeholder has no terms.");
    }

    @Deprecated
    @Override
    default IStrategoList prepend(IStrategoTerm prefix) {
        return new StrategoList(prefix, this, null);
    }
}
