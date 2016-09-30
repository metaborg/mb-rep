package org.metaborg.unification.lazy;

import org.metaborg.unification.IFindResult;
import org.metaborg.unification.ITerm;

public final class LazyFindResult implements IFindResult {

    private final ITerm rep;
    private final LazyTermUnifier unifier;

    public LazyFindResult(ITerm rep, LazyTermUnifier unifier) {
        this.rep = rep;
        this.unifier = unifier;
    }

    @Override public ITerm rep() {
        return rep;
    }

    @Override public LazyTermUnifier unifier() {
        return unifier;
    }

}