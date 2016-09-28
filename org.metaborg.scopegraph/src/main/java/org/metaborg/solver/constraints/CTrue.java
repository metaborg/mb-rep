package org.metaborg.solver.constraints;

public final class CTrue implements IConstraint {

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return "true";
    }

}