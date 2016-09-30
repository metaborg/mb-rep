package org.metaborg.unification.eager;

import org.metaborg.unification.IListTerm;
import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.NilTerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;
import org.metaborg.unification.terms.TupleTerm;

import com.google.common.collect.ImmutableList;

final class EagerFindFunction implements ITermFunction<EagerFindResult> {

    private final EagerTermUnifier unifier;

    EagerFindFunction(EagerTermUnifier unifier) {
        this.unifier = unifier;
    }

    @Override public EagerFindResult apply(TermVar term) {
        if (unifier.varReps.containsKey(term)) {
            EagerFindResult result = unifier.varReps.get(term).apply(this);
            return new EagerFindResult(result.rep(),
                    new EagerTermUnifier(result.unifier().varReps.put(term, result.rep()), result.unifier().termReps));
        } else {
            return new EagerFindResult(term, unifier);
        }
    }

    @Override public EagerFindResult apply(TermOp term) {
        if (unifier.termReps.containsKey(term)) {
            EagerFindResult result = unifier.termReps.get(term).apply(this);
            return new EagerFindResult(result.rep(),
                    new EagerTermUnifier(result.unifier().varReps, result.unifier().termReps.put(term, result.rep())));
        } else if (term.areArgsGround()) {
            // TODO Try reduction
            return new EagerFindResult(term, unifier);
        } else {
            return new EagerFindResult(term, unifier);
        }
    }

    @Override public EagerFindResult apply(ApplTerm term) {
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
                ApplTerm newTerm = new ApplTerm(term.getOp(), argBuilder.build());
                return new EagerFindResult(newTerm,
                        new EagerTermUnifier(unifier.varReps, unifier.termReps.put(term, newTerm)));
            } else {
                return new EagerFindResult(term, localUnifier);
            }
        }
    }

    @Override public EagerFindResult apply(TupleTerm term) {
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
                TupleTerm newTerm = new TupleTerm(argBuilder.build());
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

    @Override public EagerFindResult apply(ConsTerm term) {
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
                ConsTerm newTerm = new ConsTerm(head, tail);
                return new EagerFindResult(newTerm,
                        new EagerTermUnifier(unifier.varReps, unifier.termReps.put(term, newTerm)));
            } else {
                return new EagerFindResult(term, localUnifier);
            }
        }
    }

    @Override public EagerFindResult apply(NilTerm term) {
        return new EagerFindResult(term, unifier);
    }

}