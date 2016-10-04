package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class ClosureV<S> implements IRegExp<S> {

    public abstract IRegExp<S> re();

    @Override public <T> T accept(IRegExpVisitor<S,T> visitor) {
        return visitor.closure(re());
    }
    
    @Override public String toString() {
        return re() + "*";
    }

}