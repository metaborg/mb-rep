package org.metaborg.nabl2.solution;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IOccurrence {

    String namespace();

    IStrategoTerm name();

    IStrategoTerm index();

}
