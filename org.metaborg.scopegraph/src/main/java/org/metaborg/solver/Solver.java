package org.metaborg.solver;

import java.util.Collection;
import java.util.Collections;

import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.util.iterators.Iterables2;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class Solver {

    private final PriorityQueue<Branch> branchQueue;

    public Solver(ITermUnifier unifier, IConstraint... constraints) {
        this(unifier, Iterables2.from(constraints));
    }

    public Solver(ITermUnifier unifier, Iterable<IConstraint> constraints) {
        this.branchQueue = new ObjectHeapPriorityQueue<>();
        this.branchQueue.enqueue(new Branch(unifier, constraints));
    }

    public ISolution solve() {
        while (!branchQueue.isEmpty()) {
            Branch branch = branchQueue.dequeue();
            if (!branch.canProgress()) {
                return branch.solution;
            }
            for (Branch newBranch : workOnBranch(branch)) {
                branchQueue.enqueue(newBranch);
            }
        }
        return null;
    }

    private static Collection<Branch> workOnBranch(Branch branch) {
        final int errorsAtStart = branch.solution.getErrors().size();
        while (branch.canProgress()) {
            IConstraint constraint = branch.constraints.dequeue();
            Collection<SolveResult> results = constraint.accept(new SolveVisitor(branch.solution));
            if (results == null) {
                branch.defers.add(constraint);
            } else {
                branch.progress = true;
                switch (results.size()) {
                case 0:
                    throw new IllegalStateException();
                case 1: {
                    SolveResult result = results.iterator().next();
                    branch.solution = result.solution;
                    branch.addConstraints(result.constraints);
                    boolean errorsIncreased = branch.solution.getErrors().size() > errorsAtStart;
                    if (errorsIncreased) {
                        return Collections.singleton(branch);
                    }
                    break;
                }
                default: {
                    Collection<Branch> branches = Lists.newLinkedList();
                    for (SolveResult result : results) {
                        branches.add(new Branch(branch, result.solution, result.constraints));
                    }
                    return branches;
                }
                }
            }
        }
        finalizeBranch(branch);
        return Collections.singleton(branch);
    }

    private static void finalizeBranch(Branch branch) {
        assert branch.constraints.isEmpty();
        ISolution localSolution = branch.solution;
        for (IConstraint constraint : branch.defers) {
            localSolution = localSolution.setErrors(localSolution.getErrors().add("Unsolved: " + constraint));
        }
        branch.defers.clear();
    }

    private static class Branch implements Comparable<Branch> {

        public final PriorityQueue<IConstraint> constraints;
        public final ObjectSet<IConstraint> defers;
        public ISolution solution;
        public boolean progress;

        public Branch(ITermUnifier unifier, Iterable<IConstraint> constraints) {
            this.constraints = new ObjectHeapPriorityQueue<>(Iterables.toArray(constraints, IConstraint.class),
                    new ConstraintPriorityComparator());
            this.defers = new ObjectOpenHashSet<>();
            this.progress = false;
            this.solution = new Solution(unifier);
        }

        public Branch(Branch other, ISolution solution, Collection<IConstraint> constraints) {
            this.constraints = new ObjectHeapPriorityQueue<>(constraints, new ConstraintPriorityComparator());
            this.defers = new ObjectOpenHashSet<>(other.defers);
            this.progress = true;
            this.solution = solution;
        }

        public void addConstraints(Collection<IConstraint> constraints) {
            for (IConstraint constraint : constraints) {
                this.constraints.enqueue(constraint);
            }
        }

        public boolean canProgress() {
            if (!constraints.isEmpty()) {
                return true;
            }
            if (progress && !defers.isEmpty()) {
                addConstraints(defers);
                return constraints.isEmpty();
            }
            return false;
        }

        @Override public int compareTo(Branch other) {
            return solution.getErrors().size() - other.solution.getErrors().size();
        }

    }

}