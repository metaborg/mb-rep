package org.metaborg.solver.constraints;


public abstract class AConstraintVisitor<T> implements IConstraintVisitor<T> {

    @Override
    public T visit(ICTrue constraint) {
        return visitDefault(constraint);
    }

    @Override
    public T visit(ICFalse constraint) {
        return visitDefault(constraint);
    }

    @Override
    public T visit(ICEqual constraint) {
        return visitDefault(constraint);
    }

    @Override
    public T visit(ICInequal constraint) {
        return visitDefault(constraint);
    }

    @Override
    public T visit(ICConj constraint) {
        return visitDefault(constraint);
    }

    @Override
    public T visit(ICDisj constraint) {
        return visitDefault(constraint);
    }

    public abstract T visitDefault(IConstraint constraint);

}
