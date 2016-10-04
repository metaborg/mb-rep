package org.metaborg.unification.terms;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.unification.IAny;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class TermVarV implements IAny {

    public abstract @Nullable String getResource();

    public abstract String getName();

    @Override public boolean isGround() {
        return false;
    }

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply((TermVar)this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test(this);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder("?");
        if (getResource() != null) {
            sb.append(getResource());
            sb.append(".");
        }
        sb.append(getName());
        return sb.toString();
    }

}
