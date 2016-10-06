package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
abstract class ClosureV<S> implements IRegExp<S> {

    public abstract IRegExp<S> getRE();

    public abstract IRegExpBuilder<S> getBuilder();

    @Override public boolean isNullable() {
        return true;
    }

    @Override public <T> T accept(IRegExpFunction<S,T> visitor) {
        return visitor.closure(getRE());
    }

    @Override public String toString() {
        return "(" + getRE() + ")*";
    }

}