package org.metaborg.unification.eager;

import org.metaborg.unification.IListTerm;
import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.IApplTerm;
import org.metaborg.unification.terms.IConsTerm;
import org.metaborg.unification.terms.INilTerm;
import org.metaborg.unification.terms.ITermOp;
import org.metaborg.unification.terms.ITermVar;
import org.metaborg.unification.terms.ITupleTerm;
import org.metaborg.unification.terms.TupleTerm;

import com.google.common.collect.ImmutableList;

final class EagerFindFunction implements ITermFunction<EagerFindResult> {

    private final EagerTermUnifier unifier;

    EagerFindFunction(EagerTermUnifier unifier) {
        this.unifier = unifier;
    }

    @Override public EagerFindResult apply(ITermVar term) {
        if (unifier.varReps.containsKey(term)) {
            EagerFindResult result = unifier.varReps.get(term).apply(this);
            return new EagerFindResult(result.rep(),
                    new EagerTermUnifier(result.unifier().varReps.put(term, result.rep()), result.unifier().termReps));
        } else {
            return new EagerFindResult(term, unifier);
        }
    }

    @Override public EagerFindResult apply(ITermOp term) {
        if (unifier.termReps.containsKey(term)) {
            EagerFindResult result = unifier.termReps.get(term).apply(this);
            return new EagerFindResult(result.rep(),
                    new EagerTermUnifier(result.unifier().varReps, result.unifier().termReps.put(term, result.rep())));
        } else if (term.isGround()) {
            // TODO Try reduction
            return new EagerFindResult(term, unifier);
        } else {
            return new EagerFindResult(term, unifier);
        }
    }

    @Override public EagerFindResult apply(IApplTerm term) {
        if (term.isGround()) {
            return new EagerFindResult(term, unifier);
        } else if (unifier.termReps.containsKey(term)) {
            EagerFindResult result = unifier.termReps.get(term).apply(this);
            return new EagerFindResult(result.rep(),
                    new EagerTermUnifier(result.unifier().varReps, result.unifier().termReps.put(term, result.rep())));
        } else {
            ImmutableList.Builder<ITerm> argBuilder = ImmutableList.builder();
            EagerTermUnifier localUnifier = unifier;
            for (ITerm arg : term.getArgs()) {
                if (arg.isGround()) {
                    argBuilder.add(arg);
                } else {
                    EagerFindResult result = localUnifier.find(arg);
                    argBuilder.add(result.rep());
                    localUnifier = result.unifier();
                }
            }
            boolean someArgsUpdated = localUnifier != unifier;
            if (someArgsUpdated) {
                IApplTerm newTerm = ApplTerm.of(term.getOp(), argBuilder.build());
                return new EagerFindResult(newTerm,
                        new EagerTermUnifier(unifier.varReps, unifier.termReps.put(term, newTerm)));
            } else {
                return new EagerFindResult(term, localUnifier);
            }
        }
    }

    @Override public EagerFindResult apply(ITupleTerm term) {
        if (term.isGround()) {
            return new EagerFindResult(term, unifier);
        } else if (unifier.termReps.containsKey(term)) {
            EagerFindResult result = unifier.termReps.get(term).apply(this);
            return new EagerFindResult(result.rep(),
                    new EagerTermUnifier(result.unifier().varReps, result.unifier().termReps.put(term, result.rep())));
        } else {
            ImmutableList.Builder<ITerm> argBuilder = ImmutableList.builder();
            EagerTermUnifier localUnifier = unifier;
            for (ITerm arg : term.getArgs()) {
                if (arg.isGround()) {
                    argBuilder.add(arg);
                } else {
                    EagerFindResult result = localUnifier.find(arg);
                    argBuilder.add(result.rep());
                    localUnifier = result.unifier();
                }
            }
            boolean someArgsUpdated = localUnifier != unifier;
            if (someArgsUpdated) {
                ITupleTerm newTerm = TupleTerm.of(argBuilder.build());
                return new EagerFindResult(newTerm,
                        new EagerTermUnifier(unifier.varReps, unifier.termReps.put(term, newTerm)));
            } else {
                return new EagerFindResult(term, localUnifier);
            }
        }
    }

    @Override public EagerFindResult apply(IPrimitiveTerm term) {
        return new EagerFindResult(term, unifier);
    }

    @Override public EagerFindResult apply(IConsTerm term) {
        if (term.isGround()) {
            return new EagerFindResult(term, unifier);
        } else if (unifier.termReps.containsKey(term)) {
            EagerFindResult result = unifier.termReps.get(term).apply(this);
            return new EagerFindResult(result.rep(),
                    new EagerTermUnifier(result.unifier().varReps, result.unifier().termReps.put(term, result.rep())));
        } else {
            EagerTermUnifier localUnifier = unifier;
            ITerm head = term.getHead();
            if (!head.isGround()) {
                EagerFindResult result = localUnifier.find(head);
                head = result.rep();
                localUnifier = result.unifier();
            }
            IListTerm tail = term.getTail();
            if (!tail.isGround()) {
                EagerFindResult result = unifier.find(tail);
                tail = (IListTerm) result.rep();
                localUnifier = result.unifier();
            }
            boolean someArgsUpdated = localUnifier != unifier;
            if (someArgsUpdated) {
                IConsTerm newTerm = ConsTerm.of(head, tail);
                return new EagerFindResult(newTerm,
                        new EagerTermUnifier(unifier.varReps, unifier.termReps.put(term, newTerm)));
            } else {
                return new EagerFindResult(term, localUnifier);
            }
        }
    }

    @Override public EagerFindResult apply(INilTerm term) {
        return new EagerFindResult(term, unifier);
    }

}