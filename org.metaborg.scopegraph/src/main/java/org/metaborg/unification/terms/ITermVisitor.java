package org.metaborg.unification.terms;

public interface ITermVisitor<T> {

    T visit(TermVar term);

    T visit(TermOp termOp);

    T visit(ApplTerm term);

    T visit(TupleTerm term);

    T visit(IPrimitiveTerm term);

    T visit(ConsTerm term);

    T visit(NilTerm term);

}