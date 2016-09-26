package org.metaborg.solver;

public interface IConstraint {

    Iterable<ISolver> solve(ISolver solver) throws CannotSolveException;
 
}
