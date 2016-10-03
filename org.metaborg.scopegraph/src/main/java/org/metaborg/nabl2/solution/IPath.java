package org.metaborg.nabl2.solution;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IPath {

    IOccurrence reference();

    IStrategoTerm steps();

    IOccurrence declaration();

}
