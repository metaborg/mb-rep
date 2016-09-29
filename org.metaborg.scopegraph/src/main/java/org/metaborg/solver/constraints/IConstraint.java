package org.metaborg.solver.constraints;

import java.io.Serializable;

public interface IConstraint extends Serializable {

    <T> T accept(IConstraintVisitor<T> visitor);

}
