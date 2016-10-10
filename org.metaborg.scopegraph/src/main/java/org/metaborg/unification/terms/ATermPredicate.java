package org.metaborg.unification.terms;

import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermPredicate;

public abstract class ATermPredicate implements ITermPredicate {

    @Override public boolean test(ITermVar term) {
        return defaultTest(term);
    }

    @Override public boolean test(IPrimitiveTerm term) {
        return defaultTest(term);
    }

    @Override public boolean test(IApplTerm term) {
        return defaultTest(term);
    }

    @Override public boolean test(ITupleTerm term) {
        return defaultTest(term);
    }

    @Override public boolean test(IConsTerm term) {
        return defaultTest(term);
    }

    @Override public boolean test(INilTerm term) {
        return defaultTest(term);
    }

    @Override public boolean test(ITermOp term) {
        return defaultTest(term);
    }

    public abstract boolean defaultTest(ITerm term);

}