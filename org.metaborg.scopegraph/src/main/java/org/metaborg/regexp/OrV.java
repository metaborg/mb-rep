package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class OrV<S> implements IRegExp<S> {

    public abstract IRegExp<S> getLeft();

    public abstract IRegExp<S> getRight();

    @Override public <T> T accept(IRegExpVisitor<S,T> visitor) {
        return visitor.or(getLeft(), getRight());
    }
    
    @Override public String toString() {
        return "(" + getLeft() + " | " + getRight() + ")";
    }

}
