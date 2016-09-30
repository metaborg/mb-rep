package org.metaborg.unification.eager;

import org.metaborg.unification.IFindResult;
import org.metaborg.unification.ITerm;

public final class EagerFindResult implements IFindResult {

    private final ITerm rep;
    private final EagerTermUnifier unifier;

    public EagerFindResult(ITerm rep, EagerTermUnifier unifier) {
        this.rep = rep;
        this.unifier = unifier;
    }

    @Override public ITerm rep() {
        return rep;
    }

    @Override public EagerTermUnifier unifier() {
        return unifier;
    }

}