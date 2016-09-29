package org.metaborg.unification.terms;

public abstract class ATermVisitor<T> implements ITermVisitor<T> {

    @Override public T visit(TermVar term) {
        return visit((ITerm) term);
    }

    @Override public T visit(IPrimitiveTerm term) {
        return visit((ITerm) term);
    }

    @Override public T visit(ApplTerm term) {
        return visit((ITerm) term);
    }

    @Override public T visit(TupleTerm term) {
        return visit((ITerm) term);
    }

    @Override public T visit(ListTerm term) {
        return visit((ITerm) term);
    }

    @Override public T visit(OpTerm term) {
        return visit((ITerm) term);
    }

    public abstract T visit(ITerm term);

}