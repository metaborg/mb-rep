package org.metaborg.solver.constraints;

import java.util.Collection;

public class CDisj implements IConstraint {

    public final Collection<? extends IConstraint> constraints;

    public CDisj(Collection<? extends IConstraint> constraints) {
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
