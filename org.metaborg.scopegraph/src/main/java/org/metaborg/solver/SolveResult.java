package org.metaborg.solver;

import java.util.Collection;
import java.util.Collections;

import org.metaborg.solver.constraints.IConstraint;

public class SolveResult {

    public final Collection<? extends IConstraint> constraints;
    public final ISolution solution;

    public SolveResult(ISolution solution) {
        this(solution, Collections.<IConstraint> emptyList());
    }

    public SolveResult(ISolution solution, Collection<? extends IConstraint> constraints) {
        this.constraints = constraints;
        this.solution = solution;
    }

}
