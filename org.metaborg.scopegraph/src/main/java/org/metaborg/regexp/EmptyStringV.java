package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstantClass;

@Value.Immutable
@ConstantClass
@Serial.Version(value = 1L)
@SuppressWarnings("serial")
public abstract class EmptyStringV<S> implements IRegExp<S> {

    @Override public <T> T accept(IRegExpVisitor<S,T> visitor) {
        return visitor.emptyString();
    }
    
    @Override public String toString() {
        return "''";
    }
    
}
