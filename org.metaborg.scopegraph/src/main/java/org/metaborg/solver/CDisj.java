package org.metaborg.solver;

import java.util.List;

import com.google.common.collect.Lists;

public class CDisj implements IConstraint {

    public final Iterable<IConstraint> constraints;

    private CDisj(Iterable<IConstraint> constraints) {
        this.constraints = constraints;
    }

    @Override
    public Iterable<ISolver> solve(ISolver solver) {
        List<ISolver> solvers = Lists.newLinkedList();
        for(IConstraint constraint : constraints) {
           solvers.add(solver.add(constraint));
        }
        return solvers;
    }

}
