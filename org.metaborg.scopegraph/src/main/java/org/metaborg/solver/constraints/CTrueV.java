package org.metaborg.solver.constraints;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstantClass;

@Value.Immutable
@ConstantClass
@Serial.Version(value = 1L)
@SuppressWarnings("serial")
public abstract class CTrueV implements ICTrue {

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return "true";
    }

}