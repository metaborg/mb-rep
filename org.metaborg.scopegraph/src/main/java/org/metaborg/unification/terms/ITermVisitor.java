package org.metaborg.unification.terms;

public interface ITermVisitor<T> {
    T visit(TermVar term);
    T visit(IntTerm term);
    T visit(StringTerm term);
}
