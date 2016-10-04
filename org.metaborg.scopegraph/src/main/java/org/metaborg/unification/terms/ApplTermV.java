package org.metaborg.unification.terms;

import java.util.Arrays;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

import com.google.common.collect.ImmutableList;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class ApplTermV extends TermWithArgs implements ITerm {

    public abstract String getOp();

    public abstract ImmutableList<ITerm> getArgs();

    @Value.Lazy public int getArity() {
        return getArgs().size();
    }

    @Override public <T> T apply(ITermFunction<T> function) {
        return function.apply((ApplTerm) this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test((ApplTerm) this);
    }

    @Override public String toString() {
        return getOp() + "(" + super.toString() + ")";
    }

    public static ApplTerm of(String op, ITerm... args) {
        return ApplTerm.of(op, Arrays.asList(args));
    }

}