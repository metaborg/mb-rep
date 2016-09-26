package org.metaborg.solver;

import org.metaborg.unification.persistent.UnifyResult;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.util.iterators.Iterables2;

public class CEqual implements IConstraint {

    public final ITerm term1;
    public final ITerm term2;

    private CEqual(ITerm term1, ITerm term2) {
        this.term1 = term1;
        this.term2 = term2;
    }

    @Override
    public Iterable<ISolver> solve(ISolver solver) {
        UnifyResult result = solver.unifier().unify(term1, term2);
        return Iterables2.singleton(solver.setUnifier(result.unifier));
    }

}