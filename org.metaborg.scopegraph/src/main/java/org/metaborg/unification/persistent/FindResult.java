package org.metaborg.unification.persistent;

import org.metaborg.unification.terms.ITerm;

public final class FindResult {

    final public ITerm rep;
    final public PersistentTermUnifier unifier;

    public FindResult(ITerm rep, PersistentTermUnifier unifier) {
        this.rep = rep;
        this.unifier = unifier;
    }

}