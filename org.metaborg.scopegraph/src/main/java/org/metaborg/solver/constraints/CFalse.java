package org.metaborg.solver.constraints;

public final class CFalse implements IConstraint {

    private static final long serialVersionUID = 7276686859005810087L;

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return "false";
    }
}