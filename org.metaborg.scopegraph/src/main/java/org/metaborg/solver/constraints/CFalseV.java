package org.metaborg.solver.constraints;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstantClass;

@Value.Immutable
@ConstantClass
@Serial.Version(value = 1L)
@SuppressWarnings("serial")
public abstract class CFalseV implements IConstraint {

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit((CFalse)this);
    }

    @Override public String toString() {
        return "false";
    }
}