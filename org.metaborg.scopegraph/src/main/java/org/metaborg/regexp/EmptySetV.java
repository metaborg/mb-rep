package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstantClass;

@Value.Immutable
@ConstantClass
@Serial.Version(value = 1L)
@SuppressWarnings("serial")
public abstract class EmptySetV<S> implements IRegExp<S> {

    @Override public String toString() {
        return "0";
    }
    
}