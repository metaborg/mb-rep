package org.metaborg.unification.terms;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstantClass;
import org.metaborg.unification.IListTerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

@Value.Immutable
@ConstantClass
@Serial.Version(value = 1L)
@SuppressWarnings("serial")
public abstract class NilTermV implements IListTerm {

    @Override public boolean isGround() {
        return true;
    }

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply((NilTerm) this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test((NilTerm) this);
    }

    @Override public String toString() {
        return "[]";
    }

}