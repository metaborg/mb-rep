package org.metaborg.unification.eager;

import java.util.Set;

import javax.annotation.Nullable;

import org.metaborg.fastutil.persistent.Object2ObjectOpenHashPMap;
import org.metaborg.fastutil.persistent.Object2ObjectPMap;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.unification.terms.ITermVar;

public final class EagerTermUnifier implements ITermUnifier {

    final Object2ObjectPMap<ITermVar,ITerm> varReps;
    final Object2ObjectPMap<ITerm,ITerm> termReps;

    public EagerTermUnifier() {
        this.varReps = new Object2ObjectOpenHashPMap<>();
        this.termReps = new Object2ObjectOpenHashPMap<>();
    }

    EagerTermUnifier(Object2ObjectPMap<ITermVar,ITerm> varReps, Object2ObjectPMap<ITerm,ITerm> termReps) {
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
        for (ITermVar var : varReps.keySet()) {
            localUnifier = localUnifier.find(var).unifier();
        }
        return localUnifier;
    }

    @Override public Set<ITermVar> variables() {
        return varReps.keySet();
    }

}