package org.metaborg.solver.constraints;

public final class CTrue implements IConstraint {

    private static final long serialVersionUID = -6549833939972580103L;

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return "true";
    }

}