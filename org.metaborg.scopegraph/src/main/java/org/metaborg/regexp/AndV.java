package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

import com.google.common.base.Preconditions;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class AndV<S> implements IRegExp<S> {

    @Value.Check protected void check() {
        Preconditions.checkState(getLeft().getAlphabet().equals(getRight().getAlphabet()));
    }

    public abstract IRegExp<S> getLeft();

    public abstract IRegExp<S> getRight();

    public abstract IAlphabet<S> getAlphabet();

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