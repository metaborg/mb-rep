package org.spoofax.interpreter.terms;

/** 
 * A Stratego named term.
 */
public interface IStrategoNamed extends IStrategoTerm {

    /**
     * Gets the name of the term.
     *
     * @return the name of the term
     */
	String getName();

}
