package org.metaborg.unification.terms;

import java.util.Arrays;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class TupleTermV extends TermWithArgs implements ITupleTerm {

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply(this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test(this);
    }

    @Override public String toString() {
        return "(" + super.toString() + ")";
    }

    public static TupleTerm of(ITerm... args) {
        return TupleTerm.of(Arrays.asList(args));
    }

}
