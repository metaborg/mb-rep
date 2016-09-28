package org.metaborg.solver;

import org.metaborg.fastutil.persistent.PersistentObjectOpenHashSet;
import org.metaborg.fastutil.persistent.PersistentObjectSet;
import org.metaborg.unification.persistent.PersistentTermUnifier;

public final class Solution implements ISolution {

    private final PersistentTermUnifier unifier;
    private final PersistentObjectSet<String> errors;

    public Solution() {
        this.unifier = new PersistentTermUnifier();
        this.errors = new PersistentObjectOpenHashSet<>();
    }

    private Solution(PersistentTermUnifier unifier, PersistentObjectSet<String> errors) {
        this.unifier = unifier;
        this.errors = errors;
    }

    @Override public PersistentTermUnifier getUnifier() {
        return unifier;
    }

    @Override public Solution setUnifier(PersistentTermUnifier unifier) {
        return new Solution(unifier, errors);
    }

    @Override public PersistentObjectSet<String> getErrors() {
        return errors;
    }

    @Override public Solution setErrors(PersistentObjectSet<String> errors) {
        return new Solution(unifier, errors);
    }

}