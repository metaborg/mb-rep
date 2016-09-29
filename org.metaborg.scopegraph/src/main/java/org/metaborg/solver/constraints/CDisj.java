package org.metaborg.solver.constraints;

import com.google.common.collect.ImmutableList;

public class CDisj implements IConstraint {

    private static final long serialVersionUID = -6709355340712459429L;

    public final ImmutableList<? extends IConstraint> constraints;

    public CDisj(IConstraint... constraints) {
        this(ImmutableList.copyOf(constraints));
    }

    public CDisj(ImmutableList<? extends IConstraint> constraints) {
        this.constraints = constraints;
    }

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean tail = false;
        for (IConstraint constraint : constraints) {
            if (tail) {
                sb.append("; ");
            } else {
                tail = true;
            }
            sb.append(constraint);
        }
        sb.append(")");
        return sb.toString();
    }

}
