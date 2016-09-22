package org.metaborg.unification.mutable;

import org.metaborg.util.iterators.Iterables2;
import org.spoofax.interpreter.core.Pair;

public final class UnificationResult {

    public final boolean progress;
    public final Iterable<String> errors;
    public final Iterable<Pair<Rep, Rep>> remaining;

    public UnificationResult(boolean progress) {
        this(progress, Iterables2.<String>empty(), Iterables2.<Pair<Rep,Rep>>empty());
    }

    public UnificationResult(String error) {
        this(true, Iterables2.singleton(error), Iterables2.<Pair<Rep,Rep>>empty());
    }

    public UnificationResult(Iterable<String> errors) {
        this(true, errors, Iterables2.<Pair<Rep,Rep>>empty());
    }

    public UnificationResult(boolean success, Iterable<String> errors, Iterable<Pair<Rep,Rep>> remaining) {
        this.progress = success;
        this.errors = errors;
        this.remaining = remaining;
    }
    
}