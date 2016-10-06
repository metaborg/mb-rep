package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class AndV<S> implements IRegExp<S> {

    public abstract IRegExp<S> getLeft();

    public abstract IRegExp<S> getRight();

    public abstract IRegExpBuilder<S> getBuilder();

    @Value.Lazy @Override public boolean isNullable() {
        return getLeft().isNullable() && getRight().isNullable();
    }

    @Override public <T> T accept(IRegExpFunction<S,T> visitor) {
        return visitor.and(getLeft(), getRight());
    }

    @Override public String toString() {
        return "(" + getLeft() + " & " + getRight() + ")";
    }

}