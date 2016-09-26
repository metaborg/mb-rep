package org.metaborg.unification.persistent;

import org.apache.commons.lang3.tuple.Pair;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.util.iterators.Iterables2;

public final class UnifyResult {

    public final TermUnifier unifier;
    public final Iterable<Pair<ITerm,ITerm>> rest;

    public UnifyResult(TermUnifier unifier) {
        this(unifier, Iterables2.<Pair<ITerm, ITerm>>empty());
    }

    public UnifyResult(TermUnifier unifier, Iterable<Pair<ITerm, ITerm>> rest) {
        this.unifier = unifier;
        this.rest = rest;
    }

}
