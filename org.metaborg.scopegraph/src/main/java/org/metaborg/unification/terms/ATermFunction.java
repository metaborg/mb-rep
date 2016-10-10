package org.metaborg.unification.terms;

import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;

public abstract class ATermFunction<T> implements ITermFunction<T> {

    @Override public T apply(ITermVar term) {
        return defaultApply(term);
    }

    @Override public T apply(IPrimitiveTerm term) {
        return defaultApply(term);
    }

    @Override public T apply(IApplTerm term) {
        return defaultApply(term);
    }

    @Override public T apply(ITupleTerm term) {
        return defaultApply(term);
    }

    @Override public T apply(IConsTerm term) {
        return defaultApply(term);
    }

    @Override public T apply(INilTerm term) {
        return defaultApply(term);
    }

    @Override public T apply(ITermOp term) {
        return defaultApply(term);
    }

    public abstract T defaultApply(ITerm term);

}