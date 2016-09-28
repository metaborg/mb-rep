package org.metaborg.unification.persistent;

import org.metaborg.fastutil.persistent.PersistentObject2ObjectMap;
import org.metaborg.fastutil.persistent.PersistentObject2ObjectOpenHashMap;
import org.metaborg.unification.terms.ITerm;

public final class PersistentTermUnifier {

    final PersistentObject2ObjectMap<ITerm,ITerm> reps;

    public PersistentTermUnifier() {
        this.reps = new PersistentObject2ObjectOpenHashMap<>();
    }

    PersistentTermUnifier(PersistentObject2ObjectMap<ITerm,ITerm> reps) {
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