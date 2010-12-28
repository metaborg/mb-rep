package org.spoofax.interpreter.terms;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface IStrategoPlaceholder extends IStrategoTerm {

    IStrategoTerm getTemplate();

}
