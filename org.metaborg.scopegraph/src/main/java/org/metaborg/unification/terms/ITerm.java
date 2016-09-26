package org.metaborg.unification.terms;

public interface ITerm {
    <T> T accept(ITermVisitor<T> visitor);
}
