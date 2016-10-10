package org.metaborg.solver;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.metaborg.fastutil.persistent.ObjectPSet;
import org.metaborg.solver.constraints.CEqual;
import org.metaborg.solver.constraints.ICConj;
import org.metaborg.solver.constraints.ICDisj;
import org.metaborg.solver.constraints.ICEqual;
import org.metaborg.solver.constraints.ICFalse;
import org.metaborg.solver.constraints.ICInequal;
import org.metaborg.solver.constraints.ICTrue;
import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.solver.constraints.IConstraintVisitor;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.IUnifyResult;
import org.metaborg.unification.terms.TermPair;

import com.google.common.collect.Lists;

public class SolveVisitor implements IConstraintVisitor<Collection<SolveResult>> {

    private final ISolution solution;

    public SolveVisitor(ISolution solution) {
        this.solution = solution;
    }

    @Override public Collection<SolveResult> visit(ICTrue constraint) {
        return Collections.singleton(new SolveResult(solution));
    }

    @Override public Collection<SolveResult> visit(ICFalse constraint) {
        ObjectPSet<String> localErrors = solution.getErrors();
        localErrors = localErrors.add("fail");
        ISolution localSolver = solution.setErrors(localErrors);
        return Collections.singleton(new SolveResult(localSolver));
    }

    @Override public Collection<SolveResult> visit(ICConj constraint) {
        return Collections.singleton(new SolveResult(solution, constraint.getConstraints()));
    }

    @Override public Collection<SolveResult> visit(ICDisj constraint) {
        List<SolveResult> results = Lists.newLinkedList();
        for (IConstraint localConstraint : constraint.getConstraints()) {
            results.add(new SolveResult(solution, Collections.singleton(localConstraint)));
        }
        return results;
    }

    @Override public Collection<SolveResult> visit(ICEqual constraint) {
        IUnifyResult result = solution.getUnifier().unify(constraint.getFirst(), constraint.getSecond());
        if (result == null) {
            return null;
        }

        Collection<IConstraint> constraints = Lists.newLinkedList();
        for (TermPair defer : result.defers()) {
            constraints.add(CEqual.of(defer.getFirst(), defer.getSecond()));
        }

        ISolution localSolution = solution.setUnifier(result.unifier());
        ObjectPSet<String> localErrors = solution.getErrors();
        for (TermPair conflict : result.conflicts()) {
            String message = "Cannot unify " + conflict.getFirst().toString() + " with "
                    + conflict.getSecond().toString();
            localErrors = localErrors.add(message);
        }
        localSolution = localSolution.setErrors(localErrors);

        return Collections.singleton(new SolveResult(localSolution, constraints));
    }

    @Override public Collection<SolveResult> visit(ICInequal constraint) {
        ITerm rep1 = solution.getUnifier().find(constraint.getFirst()).rep();
        ITerm rep2 = solution.getUnifier().find(constraint.getSecond()).rep();
        if (!(rep1.isGround() && rep2.isGround())) {
            return null;
        }
        ISolution localSolution = solution;
        if (rep1.equals(rep2)) {
            String message = rep1 + " equals " + rep2;
            localSolution = localSolution.setErrors(localSolution.getErrors().add(message));
        }
        return Collections.singleton(new SolveResult(localSolution));
    }

}
