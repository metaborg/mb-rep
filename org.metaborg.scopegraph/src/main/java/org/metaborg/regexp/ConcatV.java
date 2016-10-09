package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
abstract class ConcatV<S> implements IRegExp<S> {

    public abstract IRegExp<S> getLeft();

    public abstract IRegExp<S> getRight();

    public abstract IRegExpBuilder<S> getBuilder();

    @Value.Lazy @Override public boolean isNullable() {
        return getLeft().isNullable() && getRight().isNullable();
    }

    @Override public <T> T accept(IRegExpFunction<S,T> visitor) {
        return visitor.concat(getLeft(), getRight());
    }

    @Override public String toString() {
        return "(" + getLeft() + " . " + getRight() + ")";
    }

}