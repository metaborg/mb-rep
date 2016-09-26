package org.metaborg.unification.persistent;

import org.metaborg.unification.persistent.fastutil.PersistentObject2ObjectMap;
import org.metaborg.unification.persistent.fastutil.PersistentObject2ObjectOpenHashMap;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.TermVar;

public final class TermUnifier {

    final PersistentObject2ObjectMap<TermVar,ITerm> reps;

    public TermUnifier() {
        this.reps = new PersistentObject2ObjectOpenHashMap<>();
    }
    
    TermUnifier(PersistentObject2ObjectMap<TermVar,ITerm> reps) {
        this.reps = reps;
    }


    public UnifyResult unify(ITerm term1, ITerm term2) {
        final FindResult result1 = find(term1);
        final FindResult result2 = result1.unifier.find(term2);
        return result1.rep.accept(new UnifyVisitor(result2.unifier, result2.rep));
    }

    public FindResult find(ITerm term) {
        return term.accept(new FindVisitor(this));
    }

}