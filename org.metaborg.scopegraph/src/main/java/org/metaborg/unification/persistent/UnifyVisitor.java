package org.metaborg.unification.persistent;

import java.util.List;

import org.metaborg.unification.terms.ATermVisitor;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.IPrimitiveTerm;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.ITermVisitor;
import org.metaborg.unification.terms.NilTerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermPair;
import org.metaborg.unification.terms.TermVar;
import org.metaborg.unification.terms.TermWithArgs;
import org.metaborg.unification.terms.TupleTerm;
import org.metaborg.util.iterators.Iterables2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

final class UnifyVisitor implements ITermVisitor<UnifyResult> {

    private final PersistentTermUnifier unifier;
    private final ITerm second;

    public UnifyVisitor(PersistentTermUnifier unifier, ITerm second) {
        this.unifier = unifier;
        this.second = second;
    }

    // ***** Var & Op *****

    @Override public UnifyResult visit(final TermVar first) {
        return UnifyResult.result(new PersistentTermUnifier(unifier.varReps.put(first, second), unifier.opReps));
    }

    public UnifyResult visit(TermOp first) {
        return null;
    };

    private class VarVisitor<T extends ITerm> extends ATermVisitor<UnifyResult> {

        protected final T first;

        public VarVisitor(T first) {
            this.first = first;
        }

        @Override public UnifyResult visit(TermVar second) {
            return UnifyResult.result(new PersistentTermUnifier(unifier.varReps.put(second, first), unifier.opReps));
        }

        @Override public UnifyResult visit(TermOp second) {
            return null;
        }

        @Override public UnifyResult visit(ITerm second) {
            return UnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
        }

    }

    // ***** Primitive *****

    @Override public UnifyResult visit(final IPrimitiveTerm first) {
        return second.accept(new PrimitiveVisitor(first));
    }

    private class PrimitiveVisitor extends VarVisitor<IPrimitiveTerm> {

        public PrimitiveVisitor(IPrimitiveTerm first) {
            super(first);
        }

        @Override public UnifyResult visit(IPrimitiveTerm second) {
            if (!first.equals(second)) {
                return UnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
            }
            return UnifyResult.result(unifier);
        }
    }

    // ***** WithArgs *****

    private UnifyResult visitArgs(TermWithArgs first, TermWithArgs second) {
        final ImmutableList<ITerm> args1 = first.getArgs();
        final ImmutableList<ITerm> args2 = second.getArgs();
        if (args1.size() != args2.size()) {
            return UnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
        }
        List<Iterable<TermPair>> conflicts = Lists.newLinkedList();
        List<Iterable<TermPair>> defers = Lists.newLinkedList();
        PersistentTermUnifier localUnifier = unifier;
        for (int i = 0; i < args1.size(); i++) {
            UnifyResult result = localUnifier.unify(args1.get(i), args2.get(i));
            if (result == null) {
                defers.add(Iterables2.singleton(TermPair.of(first, second)));
            } else {
                conflicts.add(result.conflicts);
                defers.add(result.defers);
                localUnifier = result.unifier;
            }
        }
        return UnifyResult.result(localUnifier, Iterables2.fromConcat(conflicts), Iterables2.fromConcat(defers));
    }

    // ***** Appl *****

    public UnifyResult visit(ApplTerm first) {
        return second.accept(new ApplVisitor(first));
    };

    private class ApplVisitor extends VarVisitor<ApplTerm> {

        public ApplVisitor(ApplTerm first) {
            super(first);
        }

        @Override public UnifyResult visit(ApplTerm second) {
            if (!first.getOp().equals(second.getOp())) {
                return UnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
            }
            return visitArgs(first, second);
        }
    }

    // ***** List *****

    public UnifyResult visit(ConsTerm first) {
        return second.accept(new ConsVisitor(first));
    };

    private class ConsVisitor extends VarVisitor<ConsTerm> {

        public ConsVisitor(ConsTerm first) {
            super(first);
        }

        @Override public UnifyResult visit(ConsTerm second) {
            UnifyResult heads = unifier.unify(first.getHead(), second.getHead());
            return heads.unifier.unify(first.getTail(), second.getTail());
        }

    }

    public UnifyResult visit(NilTerm first) {
        return second.accept(new NilVisitor(first));
    };

    private class NilVisitor extends VarVisitor<NilTerm> {

        public NilVisitor(NilTerm first) {
            super(first);
        }

        @Override public UnifyResult visit(NilTerm second) {
            return UnifyResult.result(unifier);
        }

    }

    // ***** Tuple*****

    public UnifyResult visit(TupleTerm first) {
        return second.accept(new TupleVisitor(first));
    };

    private class TupleVisitor extends VarVisitor<TupleTerm> {

        public TupleVisitor(TupleTerm first) {
            super(first);
        }

        @Override public UnifyResult visit(TupleTerm second) {
            return visitArgs(first, second);
        }
    }

}