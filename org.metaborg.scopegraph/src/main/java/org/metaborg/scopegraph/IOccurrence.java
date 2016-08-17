package org.metaborg.scopegraph;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IOccurrence {

    String namespace();
    IStrategoTerm name();
    IStrategoTerm index();
    
}
