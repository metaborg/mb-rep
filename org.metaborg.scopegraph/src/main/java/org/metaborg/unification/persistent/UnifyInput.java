package org.metaborg.unification.persistent;

import org.metaborg.unification.terms.ITerm;

public final class UnifyInput {

    public final TermUnifier unifier;
    public final ITerm other;

    public UnifyInput(TermUnifier unifier, ITerm other) {
        this.unifier = unifier;
        this.other = other;
    }

}
