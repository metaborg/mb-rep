package org.metaborg.solver.constraints;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

import com.google.common.collect.ImmutableList;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class CConjV implements ICConj {

    @Override
    public abstract ImmutableList<IConstraint> getConstraints();

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean tail = false;
        for (IConstraint constraint : getConstraints()) {
            if (tail) {
                sb.append(", ");
            } else {
                tail = true;
            }
            sb.append(constraint);
        }
        sb.append(")");
        return sb.toString();
    }

    public static CConj of(IConstraint... constraints) {
        return CConj.of(ImmutableList.copyOf(constraints));
    }

}
