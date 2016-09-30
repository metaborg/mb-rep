package org.metaborg.unification.lazy;

import java.util.Set;

import javax.annotation.Nullable;

import org.metaborg.fastutil.persistent.PersistentObject2ObjectMap;
import org.metaborg.fastutil.persistent.PersistentObject2ObjectOpenHashMap;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;

public final class LazyTermUnifier implements ITermUnifier {

    final PersistentObject2ObjectMap<TermVar,ITerm> varReps;
    final PersistentObject2ObjectMap<TermOp,ITerm> opReps;

    public LazyTermUnifier() {
        this.varReps = new PersistentObject2ObjectOpenHashMap<>();
        this.opReps = new PersistentObject2ObjectOpenHashMap<>();
    }

    LazyTermUnifier(PersistentObject2ObjectMap<TermVar,ITerm> varReps,
            PersistentObject2ObjectMap<TermOp,ITerm> opReps) {
        this.varReps = varReps;
        this.opReps = opReps;
    }

    @Override public @Nullable LazyUnifyResult unify(ITerm term1, ITerm term2) {
        final LazyFindResult result1 = find(term1);
        final LazyFindResult result2 = result1.unifier().find(term2);
        return result1.rep().apply(new LazyUnifyFunction(result2.unifier(), result2.rep()));
    }

    @Override public LazyFindResult find(ITerm term) {
        return term.apply(new LazyFindFunction(this));
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