package org.metaborg.unification.terms;

public abstract class ATermVisitor<T> implements ITermVisitor<T> {

    @Override
    public T visit(TermVar termVar) {
        return visit((ITerm)termVar);
    }

    @Override
    public T visit(IntTerm intTerm) {
        return visit((ITerm)intTerm);
    }

    @Override
    public T visit(StringTerm stringTerm) {
        return visit((ITerm)stringTerm);
    }

    public abstract T visit(ITerm term);

}