package org.metaborg.solver;

import org.metaborg.fastutil.persistent.ObjectOpenHashPSet;
import org.metaborg.fastutil.persistent.ObjectPSet;
import org.metaborg.unification.ITermUnifier;

public final class Solution implements ISolution {

    private final ITermUnifier unifier;
    private final ObjectPSet<String> errors;

    public Solution(ITermUnifier unifier) {
        this(unifier, new ObjectOpenHashPSet<String>());
    }

    private Solution(ITermUnifier unifier, ObjectPSet<String> errors) {
        this.unifier = unifier;
        this.errors = errors;
    }

    @Override public ITermUnifier getUnifier() {
        return unifier;
    }

    @Override public Solution setUnifier(ITermUnifier unifier) {
        return new Solution(unifier, errors);
    }

    @Override public ObjectPSet<String> getErrors() {
        return errors;
    }

    @Override public Solution setErrors(ObjectPSet<String> errors) {
        return new Solution(unifier, errors);
    }

}