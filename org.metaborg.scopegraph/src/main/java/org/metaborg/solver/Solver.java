package org.metaborg.solver;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import org.metaborg.regexp.IRegExp;
import org.metaborg.scopegraph.ILabel;
import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.util.iterators.Iterables2;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class Solver {

    private final Object2ObjectMap<Class<? extends IConstraint>,Class<? extends IConstraint>> typeRedirect;

    private final PriorityQueue<Branch> branchQueue;

    public Solver(ITermUnifier unifier, IRegExp<ILabel> wf, IConstraint... constraints) {
        this(unifier, wf, Iterables2.from(constraints));
    }

    public Solver(ITermUnifier unifier, Iterable<AbstractSolverComponent<?>> components,
            Iterable<IConstraint> constraints) {
        this.components = new Object2ObjectOpenHashMap<>();
        this.typeRedirect = new Object2ObjectOpenHashMap<>();
        for (AbstractSolverComponent<?> component : components) {
            assert findComponent(component.type()) == null;
            this.components.put(component.type(), component);
        }
        this.branchQueue = new ObjectHeapPriorityQueue<>();
        this.branchQueue.enqueue(new Branch(unifier, wf, constraints));
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
            Collection<SolveResult> results = constraint.accept(new InferVisitor(branch.solution));
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

        private final Object2ObjectMap<Class<? extends IConstraint>,AbstractSolverComponent<?>> components;
        public final PriorityQueue<IConstraint> constraints;
        public final ObjectSet<IConstraint> defers;
        public ISolution solution;
        public boolean progress;

        public Branch(ITermUnifier unifier, IRegExp<ILabel> wf, Iterable<IConstraint> constraints) {
            this.constraints = new ObjectHeapPriorityQueue<>(Iterables.toArray(constraints, IConstraint.class),
                    new ConstraintPriorityComparator());
            this.defers = new ObjectOpenHashSet<>();
            this.progress = false;
            this.solution = new Solution(unifier, wf);
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

        @SuppressWarnings("unchecked") private @Nullable AbstractSolverComponent<?> findComponent(
                final Class<? extends IConstraint> type) {
            if (components.containsKey(type)) {
                return components.get(type);
            } else if (typeRedirect.containsKey(type)) {
                return findComponent(typeRedirect.get(type));
            } else {
                AbstractSolverComponent<?> component = null;
                for (Class<?> intf : type.getInterfaces()) {
                    if (IConstraint.class.isAssignableFrom(intf)) {
                        component = findComponent(type);
                        break;
                    }
                }
                if (component == null) {
                    Class<?> superType = type.getSuperclass();
                    if (superType != null && IConstraint.class.isAssignableFrom(superType)) {
                        component = findComponent(type);
                    }
                }
                if (component != null) {
                    typeRedirect.put(type, component.type());
                }
                return component;
            }
        }

    }

}