package org.metaborg.unification.terms;

import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;

public abstract class ATermFunction<T> implements ITermFunction<T> {

    @Override public T apply(TermVar term) {
        return defaultApply((ITerm) term);
    }

    @Override public T apply(IPrimitiveTerm term) {
        return defaultApply((ITerm) term);
    }

    @Override public T apply(ApplTerm term) {
        return defaultApply((ITerm) term);
    }

    @Override public T apply(TupleTerm term) {
        return defaultApply((ITerm) term);
    }

    @Override public T apply(ConsTerm term) {
        return defaultApply((ITerm) term);
    }

    @Override public T apply(NilTerm term) {
        return defaultApply((ITerm) term);
    }

    @Override public T apply(TermOp term) {
        return defaultApply((ITerm) term);
    }

    public abstract T defaultApply(ITerm term);

}