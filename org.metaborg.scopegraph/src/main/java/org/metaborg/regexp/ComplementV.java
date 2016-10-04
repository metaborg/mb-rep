package org.metaborg.regexp;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class ComplementV<S> implements IRegExp<S> {

    public abstract IRegExp<S> re();

    @Override public String toString() {
        return "~" + re();
    }

}
