package org.metaborg.solver;

import org.metaborg.util.iterators.Iterables2;

public final class CTrue implements IConstraint {

    @Override
    public Iterable<ISolver> solve(ISolver solver) {
        return Iterables2.singleton(solver);
    }

}