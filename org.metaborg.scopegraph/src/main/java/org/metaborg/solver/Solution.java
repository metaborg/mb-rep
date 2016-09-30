package org.metaborg.solver;

import org.metaborg.fastutil.persistent.PersistentObjectOpenHashSet;
import org.metaborg.fastutil.persistent.PersistentObjectSet;
import org.metaborg.unification.ITermUnifier;

public final class Solution implements ISolution {

    private final ITermUnifier unifier;
    private final PersistentObjectSet<String> errors;

    public Solution(ITermUnifier unifier) {
        this(unifier, new PersistentObjectOpenHashSet<String>());
    }

    private Solution(ITermUnifier unifier, PersistentObjectSet<String> errors) {
        this.unifier = unifier;
        this.errors = errors;
    }

    @Override public ITermUnifier getUnifier() {
        return unifier;
    }

    @Override public Solution setUnifier(ITermUnifier unifier) {
        return new Solution(unifier, errors);
    }

    @Override public PersistentObjectSet<String> getErrors() {
        return errors;
    }

    @Override public Solution setErrors(PersistentObjectSet<String> errors) {
        return new Solution(unifier, errors);
    }

}