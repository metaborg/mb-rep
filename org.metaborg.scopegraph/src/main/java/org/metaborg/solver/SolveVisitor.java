package org.metaborg.solver;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.metaborg.fastutil.persistent.PersistentObjectSet;
import org.metaborg.solver.constraints.CConj;
import org.metaborg.solver.constraints.CDisj;
import org.metaborg.solver.constraints.CEqual;
import org.metaborg.solver.constraints.CFalse;
import org.metaborg.solver.constraints.CTrue;
import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.solver.constraints.IConstraintVisitor;
import org.metaborg.unification.persistent.UnifyResult;
import org.metaborg.unification.terms.TermPair;

import com.google.common.collect.Lists;


public class SolveVisitor implements IConstraintVisitor<Collection<SolveResult>> {

    private final ISolution solution;

    public SolveVisitor(ISolution solution) {
        this.solution = solution;
    }

    @Override public Collection<SolveResult> visit(CTrue constraint) {
        return Collections.singleton(new SolveResult(solution));
    }

    @Override public Collection<SolveResult> visit(CFalse constraint) {
        PersistentObjectSet<String> localErrors = solution.getErrors();
        localErrors = localErrors.add("fail");
        ISolution localSolver = solution.setErrors(localErrors);
        return Collections.singleton(new SolveResult(localSolver));
    }

    @Override public Collection<SolveResult> visit(CConj constraint) {
        return Collections.singleton(new SolveResult(solution, constraint.constraints));
    }

    @Override public Collection<SolveResult> visit(CDisj constraint) {
        List<SolveResult> results = Lists.newLinkedList();
        for (IConstraint localConstraint : constraint.constraints) {
            results.add(new SolveResult(solution, Collections.singleton(localConstraint)));
        }
        return results;
    }

    @Override public Collection<SolveResult> visit(CEqual constraint) {
        UnifyResult result = solution.getUnifier().unify(constraint.term1, constraint.term2);
        if (result == null) {
            return null;
        }

        Collection<IConstraint> constraints = Lists.newLinkedList();
        for (TermPair defer : result.defers) {
            constraints.add(new CEqual(defer.first, defer.second));
        }

        ISolution localSolution = solution.setUnifier(result.unifier);
        PersistentObjectSet<String> localErrors = solution.getErrors();
        for (TermPair conflict : result.conflicts) {
            String message = "Cannot unify " + conflict.first.toString() + " with " + conflict.second.toString();
            localErrors = localErrors.add(message);
        }
        localSolution = localSolution.setErrors(localErrors);

        return Collections.singleton(new SolveResult(localSolution, constraints));
    }

}
