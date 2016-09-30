package org.metaborg.unification.lazy;

import org.metaborg.unification.IUnifyResult;
import org.metaborg.unification.terms.TermPair;
import org.metaborg.util.iterators.Iterables2;

final class LazyUnifyResult implements IUnifyResult {

    private final LazyTermUnifier unifier;
    private final Iterable<TermPair> conflicts;
    private final Iterable<TermPair> defers;

    private LazyUnifyResult(LazyTermUnifier unifier, Iterable<TermPair> conflicts, Iterable<TermPair> defers) {
        this.unifier = unifier;
        this.conflicts = conflicts;
        this.defers = defers;
    }

    @Override public LazyTermUnifier unifier() {
        return unifier;
    }

    @Override public Iterable<TermPair> conflicts() {
        return conflicts;
    }

    @Override public Iterable<TermPair> defers() {
        return defers;
    }

    public static LazyUnifyResult result(LazyTermUnifier unifier) {
        return new LazyUnifyResult(unifier, EMPTY, EMPTY);
    }

    public static LazyUnifyResult resultWithConflict(LazyTermUnifier unifier, TermPair conflict) {
        return new LazyUnifyResult(unifier, Iterables2.singleton(conflict), EMPTY);
    }

    public static LazyUnifyResult resultWithConflicts(LazyTermUnifier unifier, Iterable<TermPair> conflicts) {
        return new LazyUnifyResult(unifier, conflicts, EMPTY);
    }

    public static LazyUnifyResult resultWithDefer(LazyTermUnifier unifier, TermPair defer) {
        return new LazyUnifyResult(unifier, EMPTY, Iterables2.singleton(defer));
    }

    public static LazyUnifyResult resultWithDefers(LazyTermUnifier unifier, Iterable<TermPair> defers) {
        return new LazyUnifyResult(unifier, EMPTY, defers);
    }

    public static LazyUnifyResult result(LazyTermUnifier unifier, Iterable<TermPair> conflicts,
            Iterable<TermPair> deferred) {
        return new LazyUnifyResult(unifier, conflicts, deferred);
    }

}