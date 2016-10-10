package org.metaborg.solver.constraints;


public interface IConstraintVisitor<T> {

    T visit(ICTrue constraint);

    T visit(ICFalse constraint);

    T visit(ICEqual constraint);

    T visit(ICInequal constraint);

    T visit(ICConj constraint);

    T visit(ICDisj constraint);

}
