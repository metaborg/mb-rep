package org.metaborg.unification.terms;

public interface ITermVisitor<T> {

    T visit(TermVar term);

    T visit(OpTerm opTerm);

    T visit(ApplTerm term);

    T visit(TupleTerm term);

    T visit(IPrimitiveTerm term);

    T visit(ListTerm listTerm);

}