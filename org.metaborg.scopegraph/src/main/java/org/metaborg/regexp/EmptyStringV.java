package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Version(value = 1L)
@SuppressWarnings("serial")
abstract class EmptyStringV<S> implements IRegExp<S> {

    public abstract IAlphabet<S> getAlphabet();

    @Override public boolean isNullable() {
        return true;
    }

    @Override public <T> T accept(IRegExpFunction<S,T> visitor) {
        return visitor.emptyString();
    }

    @Override public String toString() {
        return "e";
    }

}
