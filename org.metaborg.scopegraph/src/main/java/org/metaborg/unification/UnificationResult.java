package org.metaborg.unification;

import org.metaborg.util.iterators.Iterables2;
import org.spoofax.interpreter.core.Pair;

public abstract class UnificationResult {

    public abstract Iterable<String> errors();
    public abstract Iterable<Pair<Rep, Rep>> deferred();

    public static UnificationResult success() {
        return new UnificationResult() {
            @Override public Iterable<String> errors() {
                return Iterables2.empty();
            }
            @Override public Iterable<Pair<Rep, Rep>> deferred() {
                return Iterables2.empty();
            }
        };
    }

    public static UnificationResult defer(final Rep r1, final Rep r2) {
        return new UnificationResult() {
            @Override public Iterable<String> errors() {
                return Iterables2.empty();
            }
            @Override public Iterable<Pair<Rep, Rep>> deferred() {
                return Iterables2.singleton(new Pair<Rep,Rep>(r1, r2));
            }
        };
    }

    public static UnificationResult failure(final String message) {
        return new UnificationResult() {
            @Override public Iterable<String> errors() {
                return Iterables2.singleton(message);
            }
            @Override public Iterable<Pair<Rep, Rep>> deferred() {
                return Iterables2.empty();
            }
        };
    }

    public static UnificationResult combine(UnificationResult[] results) {
        final Iterable<String>[] errorsArray = new Iterable[results.length];
        for(int i = 0; i < results.length; i++) {
            errorsArray[i] = results[i].errors();
        }
        final Iterable<String> errors = Iterables2.fromConcat(errorsArray);

        final Iterable<Pair<Rep,Rep>>[] deferredArray = new Iterable[results.length];
        for(int i = 0; i < results.length; i++) {
            deferredArray[i] = results[i].deferred();
        }
        final Iterable<Pair<Rep,Rep>> deferred = Iterables2.fromConcat(deferredArray);

        return new UnificationResult() {
            @Override public Iterable<String> errors() {
                return errors;
            }
            @Override public Iterable<Pair<Rep, Rep>> deferred() {
                return deferred;
            }
        };
    }
 
}