package org.metaborg.unification.persistent;

import org.metaborg.unification.terms.ATermVisitor;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.TermVar;

final class FindVisitor extends ATermVisitor<FindResult> {

    private final TermUnifier unifier;
    
    FindVisitor(TermUnifier unifier) {
        this.unifier = unifier;
    }

    @Override public FindResult visit(TermVar termVar) {
        if(unifier.reps.containsKey(termVar)) {
            FindResult result = unifier.reps.get(termVar).accept(this);
            return new FindResult(result.rep, new TermUnifier(result.unifier.reps.put(termVar, result.rep)));
        } else {
            return new FindResult(termVar, unifier);
        }
    }

    @Override public FindResult visit(ITerm term) {
        return new FindResult(term, unifier);
    }

}