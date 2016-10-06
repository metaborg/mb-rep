package org.metaborg.solver.constraints;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

import com.google.common.collect.ImmutableList;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class CDisjV implements IConstraint {

    public abstract ImmutableList<IConstraint> getConstraints();

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit((CDisj) this);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean tail = false;
        for (IConstraint constraint : getConstraints()) {
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

    public static CDisj of(IConstraint... constraints) {
        return CDisj.of(ImmutableList.copyOf(constraints));
    }

}
