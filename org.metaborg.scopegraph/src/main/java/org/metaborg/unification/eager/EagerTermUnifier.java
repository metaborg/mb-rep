package org.metaborg.unification.eager;

import java.util.Set;

import javax.annotation.Nullable;

import org.metaborg.fastutil.persistent.PersistentObject2ObjectMap;
import org.metaborg.fastutil.persistent.PersistentObject2ObjectOpenHashMap;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.unification.terms.TermVar;

public final class EagerTermUnifier implements ITermUnifier {

    final PersistentObject2ObjectMap<TermVar,ITerm> varReps;
    final PersistentObject2ObjectMap<ITerm,ITerm> termReps;

    public EagerTermUnifier() {
        this.varReps = new PersistentObject2ObjectOpenHashMap<>();
        this.termReps = new PersistentObject2ObjectOpenHashMap<>();
    }

    EagerTermUnifier(PersistentObject2ObjectMap<TermVar,ITerm> varReps,
            PersistentObject2ObjectMap<ITerm,ITerm> termReps) {
        this.varReps = varReps;
        this.termReps = termReps;
    }

    @Override public @Nullable EagerUnifyResult unify(ITerm term1, ITerm term2) {
        final EagerFindResult result1 = find(term1);
        final EagerFindResult result2 = result1.unifier().find(term2);
        return result1.rep().apply(new EagerUnifyFunction(result2.unifier(), result2.rep()));
    }

    @Override public EagerFindResult find(ITerm term) {
        return term.apply(new EagerFindFunction(this));
    }

    @Override public ITermUnifier findAll() {
        ITermUnifier localUnifier = this;
        for (TermVar var : varReps.keySet()) {
            localUnifier = localUnifier.find(var).unifier();
        }
        return localUnifier;
    }

    @Override public Set<TermVar> variables() {
        return varReps.keySet();
    }

}