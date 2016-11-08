package org.metaborg.solver;

import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.unification.ITerm;
import org.spoofax.terms.util.NotImplementedException;

public abstract class AbstractSolverComponent<C extends IConstraint> {

    final protected ITerm find(ITerm term) {
        throw new NotImplementedException();
    }

    final protected void addMessage(String message) {
        throw new NotImplementedException();
    }

    abstract Iterable<SolveResult> solve(C constraint);

    abstract void done(Iterable<C> constraints);

    abstract Class<? extends C> type();

    final class SolveResult {

        AbstractSolverComponent<C> solutions() {
            throw new NotImplementedException();
        }

        Iterable<IConstraint> constraints() {
            throw new NotImplementedException();
        }
    }

}