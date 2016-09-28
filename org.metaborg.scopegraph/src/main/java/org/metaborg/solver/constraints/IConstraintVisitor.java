package org.metaborg.solver.constraints;


public interface IConstraintVisitor<T> {

    T visit(CTrue constraint);

    T visit(CFalse constraint);

    T visit(CEqual constraint);

    T visit(CConj constraint);

    T visit(CDisj constraint);

}
