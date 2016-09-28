package org.metaborg.solver.constraints;


public abstract class AConstraintVisitor<T> implements IConstraintVisitor<T> {

    @Override public T visit(CTrue constraint) {
        return visit((IConstraint) constraint);
    }

    @Override public T visit(CFalse constraint) {
        return visit((IConstraint) constraint);
    }

    @Override public T visit(CEqual constraint) {
        return visit((IConstraint) constraint);
    }

    @Override public T visit(CConj constraint) {
        return visit((IConstraint) constraint);
    }

    @Override public T visit(CDisj constraint) {
        return visit((IConstraint) constraint);
    }

    public abstract T visit(IConstraint constraint);

}
