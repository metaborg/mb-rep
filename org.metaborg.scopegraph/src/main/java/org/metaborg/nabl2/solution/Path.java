package org.metaborg.nabl2.solution;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoTerm;

public class Path implements IPath, Serializable {

    private static final long serialVersionUID = 5440515176075755729L;

    private final IOccurrence ref;
    private final IOccurrence decl;
    private final IStrategoTerm steps;

    public Path(IOccurrence ref, IOccurrence decl, IStrategoTerm steps) {
        this.ref = ref;
        this.decl = decl;
        this.steps = steps;
    }

    @Override public IOccurrence reference() {
        return ref;
    }

    @Override public IOccurrence declaration() {
        return decl;
    }

    @Override public IStrategoTerm steps() {
        return steps;
    }

}
