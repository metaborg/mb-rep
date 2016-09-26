package org.metaborg.solver;

import org.metaborg.util.iterators.Iterables2;

public class CConj implements IConstraint {

    public final Iterable<IConstraint> constraints;

    private CConj(Iterable<IConstraint> constraints) {
        this.constraints = constraints;
    }

    @Override
    public Iterable<ISolver> solve(ISolver solver) {
        return Iterables2.singleton(solver.addAll(constraints));
    }

}
