package org.metaborg.unification.eager;

import org.metaborg.unification.IUnifyResult;
import org.metaborg.unification.terms.TermPair;
import org.metaborg.util.iterators.Iterables2;

final class EagerUnifyResult implements IUnifyResult {

    private final EagerTermUnifier unifier;
    private final Iterable<TermPair> conflicts;
    private final Iterable<TermPair> defers;

    private EagerUnifyResult(EagerTermUnifier unifier, Iterable<TermPair> conflicts, Iterable<TermPair> defers) {
        this.unifier = unifier;
        this.conflicts = conflicts;
        this.defers = defers;
    }

    @Override public EagerTermUnifier unifier() {
        return unifier;
    }

    @Override public Iterable<TermPair> conflicts() {
        return conflicts;
    }

    @Override public Iterable<TermPair> defers() {
        return defers;
    }

    public static EagerUnifyResult result(EagerTermUnifier unifier) {
        return new EagerUnifyResult(unifier, EMPTY, EMPTY);
    }

    public static EagerUnifyResult resultWithConflict(EagerTermUnifier unifier, TermPair conflict) {
        return new EagerUnifyResult(unifier, Iterables2.singleton(conflict), EMPTY);
    }

    public static EagerUnifyResult resultWithConflicts(EagerTermUnifier unifier, Iterable<TermPair> conflicts) {
        return new EagerUnifyResult(unifier, conflicts, EMPTY);
    }

    public static EagerUnifyResult resultWithDefer(EagerTermUnifier unifier, TermPair defer) {
        return new EagerUnifyResult(unifier, EMPTY, Iterables2.singleton(defer));
    }

    public static EagerUnifyResult resultWithDefers(EagerTermUnifier unifier, Iterable<TermPair> defers) {
        return new EagerUnifyResult(unifier, EMPTY, defers);
    }

    public static EagerUnifyResult result(EagerTermUnifier unifier, Iterable<TermPair> conflicts,
            Iterable<TermPair> deferred) {
        return new EagerUnifyResult(unifier, conflicts, deferred);
    }

}