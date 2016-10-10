package org.metaborg.unification.eager;

import java.util.List;

import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.terms.ATermFunction;
import org.metaborg.unification.terms.IApplTerm;
import org.metaborg.unification.terms.IConsTerm;
import org.metaborg.unification.terms.INilTerm;
import org.metaborg.unification.terms.ITermOp;
import org.metaborg.unification.terms.ITermVar;
import org.metaborg.unification.terms.ITermWithArgs;
import org.metaborg.unification.terms.ITupleTerm;
import org.metaborg.unification.terms.TermPair;
import org.metaborg.util.iterators.Iterables2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

final class EagerUnifyFunction implements ITermFunction<EagerUnifyResult> {

    private final EagerTermUnifier unifier;
    private final ITerm second;

    public EagerUnifyFunction(EagerTermUnifier unifier, ITerm second) {
        this.unifier = unifier;
        this.second = second;
    }

    // ***** Var & Op *****

    @Override public EagerUnifyResult apply(final ITermVar first) {
        return EagerUnifyResult.result(new EagerTermUnifier(unifier.varReps.put(first, second), unifier.termReps));
    }

    public EagerUnifyResult apply(ITermOp first) {
        return null;
    };

    private class VarVisitor<T extends ITerm> extends ATermFunction<EagerUnifyResult> {

        protected final T first;

        public VarVisitor(T first) {
            this.first = first;
        }

        @Override public EagerUnifyResult apply(ITermVar second) {
            return EagerUnifyResult.result(new EagerTermUnifier(unifier.varReps.put(second, first), unifier.termReps));
        }

        @Override public EagerUnifyResult apply(ITermOp second) {
            return null;
        }

        @Override public EagerUnifyResult defaultApply(ITerm second) {
            return EagerUnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
        }

    }

    // ***** Primitive *****

    @Override public EagerUnifyResult apply(final IPrimitiveTerm first) {
        return second.apply(new PrimitiveVisitor(first));
    }

    private class PrimitiveVisitor extends VarVisitor<IPrimitiveTerm> {

        public PrimitiveVisitor(IPrimitiveTerm first) {
            super(first);
        }

        @Override public EagerUnifyResult apply(IPrimitiveTerm second) {
            if (!first.equals(second)) {
                return EagerUnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
            }
            return EagerUnifyResult.result(unifier);
        }
    }

    // ***** WithArgs *****

    private EagerUnifyResult visitArgs(ITermWithArgs first, ITermWithArgs second) {
        final ImmutableList<ITerm> args1 = first.getArgs();
        final ImmutableList<ITerm> args2 = second.getArgs();
        if (args1.size() != args2.size()) {
            return EagerUnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
        }
        List<Iterable<TermPair>> conflicts = Lists.newLinkedList();
        List<Iterable<TermPair>> defers = Lists.newLinkedList();
        EagerTermUnifier localUnifier = unifier;
        for (int i = 0; i < args1.size(); i++) {
            EagerUnifyResult result = localUnifier.unify(args1.get(i), args2.get(i));
            if (result == null) {
                defers.add(Iterables2.singleton(TermPair.of(first, second)));
            } else {
                conflicts.add(result.conflicts());
                defers.add(result.defers());
                localUnifier = result.unifier();
            }
        }
        return EagerUnifyResult.result(localUnifier, Iterables2.fromConcat(conflicts), Iterables2.fromConcat(defers));
    }

    // ***** Appl *****

    public EagerUnifyResult apply(IApplTerm first) {
        return second.apply(new ApplVisitor(first));
    };

    private class ApplVisitor extends VarVisitor<IApplTerm> {

        public ApplVisitor(IApplTerm first) {
            super(first);
        }

        @Override public EagerUnifyResult apply(IApplTerm second) {
            if (!first.getOp().equals(second.getOp())) {
                return EagerUnifyResult.resultWithConflict(unifier, TermPair.of(first, second));
            }
            return visitArgs(first, second);
        }
    }

    // ***** List *****

    public EagerUnifyResult apply(IConsTerm first) {
        return second.apply(new ConsVisitor(first));
    };

    private class ConsVisitor extends VarVisitor<IConsTerm> {

        public ConsVisitor(IConsTerm first) {
            super(first);
        }

        @Override public EagerUnifyResult apply(IConsTerm second) {
            EagerUnifyResult heads = unifier.unify(first.getHead(), second.getHead());
            return heads.unifier().unify(first.getTail(), second.getTail());
        }

    }

    public EagerUnifyResult apply(INilTerm first) {
        return second.apply(new NilVisitor(first));
    };

    private class NilVisitor extends VarVisitor<INilTerm> {

        public NilVisitor(INilTerm first) {
            super(first);
        }

        @Override public EagerUnifyResult apply(INilTerm second) {
            return EagerUnifyResult.result(unifier);
        }

    }

    // ***** Tuple*****

    public EagerUnifyResult apply(ITupleTerm first) {
        return second.apply(new TupleVisitor(first));
    };

    private class TupleVisitor extends VarVisitor<ITupleTerm> {

        public TupleVisitor(ITupleTerm first) {
            super(first);
        }

        @Override public EagerUnifyResult apply(ITupleTerm second) {
            return visitArgs(first, second);
        }
    }

}