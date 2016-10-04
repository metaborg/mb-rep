package org.metaborg.unification.terms;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class DoubleTermV implements IPrimitiveTerm {

    public abstract double getValue();

    @Override public boolean isGround() {
        return true;
    }

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply(this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test(this);
    }

    @Override public java.lang.String toString() {
        return Double.toString(getValue());
    }

}