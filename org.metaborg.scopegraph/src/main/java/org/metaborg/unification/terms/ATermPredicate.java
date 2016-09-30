package org.metaborg.unification.terms;

import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermPredicate;

public abstract class ATermPredicate implements ITermPredicate {

    @Override public boolean test(TermVar term) {
        return defaultTest((ITerm) term);
    }

    @Override public boolean test(IPrimitiveTerm term) {
        return defaultTest((ITerm) term);
    }

    @Override public boolean test(ApplTerm term) {
        return defaultTest((ITerm) term);
    }

    @Override public boolean test(TupleTerm term) {
        return defaultTest((ITerm) term);
    }

    @Override public boolean test(ConsTerm term) {
        return defaultTest((ITerm) term);
    }

    @Override public boolean test(NilTerm term) {
        return defaultTest((ITerm) term);
    }

    @Override public boolean test(TermOp term) {
        return defaultTest((ITerm) term);
    }

    public abstract boolean defaultTest(ITerm term);

}