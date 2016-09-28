package org.metaborg.unification.persistent;

import org.metaborg.unification.terms.TermPair;
import org.metaborg.util.iterators.Iterables2;

public final class UnifyResult {

    public static final Iterable<TermPair> EMPTY = Iterables2.empty();

    public final PersistentTermUnifier unifier;
    public final Iterable<TermPair> conflicts;
    public final Iterable<TermPair> defers;

    private UnifyResult(PersistentTermUnifier unifier, Iterable<TermPair> conflicts, Iterable<TermPair> defers) {
        this.unifier = unifier;
        this.conflicts = conflicts;
        this.defers = defers;
    }

    public static UnifyResult result(PersistentTermUnifier unifier) {
        return new UnifyResult(unifier, EMPTY, EMPTY);
    }

    public static UnifyResult resultWithConflict(PersistentTermUnifier unifier, TermPair conflict) {
        return new UnifyResult(unifier, Iterables2.singleton(conflict), EMPTY);
    }

    public static UnifyResult resultWithConflicts(PersistentTermUnifier unifier, Iterable<TermPair> conflicts) {
        return new UnifyResult(unifier, conflicts, EMPTY);
    }

    public static UnifyResult resultWithDefer(PersistentTermUnifier unifier, TermPair defer) {
        return new UnifyResult(unifier, EMPTY, Iterables2.singleton(defer));
    }

    public static UnifyResult resultWithDefers(PersistentTermUnifier unifier, Iterable<TermPair> defers) {
        return new UnifyResult(unifier, EMPTY, defers);
    }

    public static UnifyResult result(PersistentTermUnifier unifier, Iterable<TermPair> conflicts,
            Iterable<TermPair> deferred) {
        return new UnifyResult(unifier, conflicts, deferred);
    }

}