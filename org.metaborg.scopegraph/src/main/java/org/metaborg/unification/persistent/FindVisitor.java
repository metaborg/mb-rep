package org.metaborg.unification.persistent;

import org.metaborg.unification.terms.ATermVisitor;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;

final class FindVisitor extends ATermVisitor<FindResult> {

    private final PersistentTermUnifier unifier;

    FindVisitor(PersistentTermUnifier unifier) {
        this.unifier = unifier;
    }

    @Override public FindResult visit(TermVar termVar) {
        if (unifier.varReps.containsKey(termVar)) {
            FindResult result = unifier.varReps.get(termVar).accept(this);
            return new FindResult(result.rep,
                    new PersistentTermUnifier(result.unifier.varReps.put(termVar, result.rep), result.unifier.opReps));
        } else {
            return new FindResult(termVar, unifier);
        }
    }

    @Override public FindResult visit(TermOp termOp) {
        if (unifier.opReps.containsKey(termOp)) {
            FindResult result = unifier.opReps.get(termOp).accept(this);
            return new FindResult(result.rep,
                    new PersistentTermUnifier(result.unifier.varReps, result.unifier.opReps.put(termOp, result.rep)));
        } else {
            return new FindResult(termOp, unifier);
        }
    }

    @Override public FindResult visit(ITerm term) {
        return new FindResult(term, unifier);
    }

}