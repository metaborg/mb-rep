package org.metaborg.solver.constraints;

public interface IConstraint {

    <T> T accept(IConstraintVisitor<T> visitor);

}
