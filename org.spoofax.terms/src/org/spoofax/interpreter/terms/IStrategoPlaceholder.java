package org.spoofax.interpreter.terms;

/**
 * A Stratego Placeholder term.
 *
 * A placeholder indicates a place within a term where terms may be substituted or matched.
 * The template of a placeholder indicates the term being matched, deconstructed, or constructed.
 * They are most useful in the C implementation of ATerms, and are not really used in the Java
 * implementation.
 */
public interface IStrategoPlaceholder extends IStrategoTerm {

    /**
     * Gets the template of the placeholder.
     *
     * @return the template
     */
    IStrategoTerm getTemplate();

}
