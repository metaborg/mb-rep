package org.metaborg.solver.constraints;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.unification.ITerm;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class CInequalV implements ICInequal {

    public abstract ITerm getFirst();

    public abstract ITerm getSecond();

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return getFirst() + " == " + getSecond();
    }

}