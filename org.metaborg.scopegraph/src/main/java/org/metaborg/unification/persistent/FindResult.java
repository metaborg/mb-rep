package org.metaborg.unification.persistent;

import org.metaborg.unification.terms.ITerm;

public final class FindResult {

    final ITerm rep;
    final TermUnifier unifier;

    public FindResult(ITerm rep, TermUnifier unifier) {
        this.rep = rep;
        this.unifier = unifier;
    }

}