package org.metaborg.scopegraph;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IPath {

    IOccurrence reference();

    IStrategoTerm steps();

    IOccurrence declaration();

}
