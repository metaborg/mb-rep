package org.metaborg.unification.terms;

import java.io.Serializable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.unification.ITerm;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class TermPairV implements Serializable {

    public abstract ITerm getFirst();

    public abstract ITerm getSecond();

    @Override public String toString() {
        return "(" + getFirst() + "," + getSecond() + ")";
    }

}