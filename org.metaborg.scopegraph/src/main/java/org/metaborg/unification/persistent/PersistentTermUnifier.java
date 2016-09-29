package org.metaborg.unification.persistent;

import java.util.Set;

import javax.annotation.Nullable;

import org.metaborg.fastutil.persistent.PersistentObject2ObjectMap;
import org.metaborg.fastutil.persistent.PersistentObject2ObjectOpenHashMap;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;

public final class PersistentTermUnifier {

    final PersistentObject2ObjectMap<TermVar,ITerm> varReps;
    final PersistentObject2ObjectMap<TermOp,ITerm> opReps;

    public PersistentTermUnifier() {
        this.varReps = new PersistentObject2ObjectOpenHashMap<>();
        this.opReps = new PersistentObject2ObjectOpenHashMap<>();
    }

    PersistentTermUnifier(PersistentObject2ObjectMap<TermVar,ITerm> varReps,
            PersistentObject2ObjectMap<TermOp,ITerm> opReps) {
        this.varReps = varReps;
        this.opReps = opReps;
    }

    public @Nullable UnifyResult unify(ITerm term1, ITerm term2) {
        final FindResult result1 = find(term1);
        final FindResult result2 = result1.unifier.find(term2);
        return result1.rep.accept(new UnifyVisitor(result2.unifier, result2.rep));
    }

    public FindResult find(ITerm term) {
        return term.accept(new FindVisitor(this));
    }

    public PersistentTermUnifier findAll() {
        PersistentTermUnifier localUnifier = this;
        for (TermVar var : varReps.keySet()) {
            localUnifier = localUnifier.find(var).unifier;
        }
        return localUnifier;
    }

    public Set<TermVar> variables() {
        return varReps.keySet();
    }

}