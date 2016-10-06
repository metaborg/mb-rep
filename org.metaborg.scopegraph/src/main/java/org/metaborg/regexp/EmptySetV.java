package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Version(value = 1L)
@SuppressWarnings("serial")
abstract class EmptySetV<S> implements IRegExp<S> {

    public abstract IAlphabet<S> getAlphabet();

    @Override public boolean isNullable() {
        return false;
    }

    @Override public <T> T accept(IRegExpFunction<S,T> visitor) {
        return visitor.emptySet();
    }

    @Override public String toString() {
        return "0";
    }

}