package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
abstract class ComplementV<S> implements IRegExp<S> {

    public abstract IRegExp<S> getRE();

    public abstract IRegExpBuilder<S> getBuilder();

    @Value.Lazy @Override public boolean isNullable() {
        return !getRE().isNullable();
    }

    @Override public <T> T accept(IRegExpFunction<S,T> visitor) {
        return visitor.complement(getRE());
    }

    @Override public String toString() {
        return "~(" + getRE() + ")";
    }

}
