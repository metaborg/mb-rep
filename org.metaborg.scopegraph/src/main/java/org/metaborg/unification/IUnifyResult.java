package org.metaborg.unification;

import org.metaborg.unification.terms.TermPair;
import org.metaborg.util.iterators.Iterables2;

public interface IUnifyResult {

    Iterable<TermPair> EMPTY = Iterables2.empty();

    ITermUnifier unifier();

    Iterable<TermPair> conflicts();

    Iterable<TermPair> defers();

}