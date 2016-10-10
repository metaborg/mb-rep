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
public abstract class TermOpV extends TermWithArgs implements ITermOp {

    @Override public abstract String getOp();

    @Override public abstract ImmutableList<ITerm> getArgs();

    @Override @Value.Lazy public int getArity() {
        return getArgs().size();
    }

    @Override public boolean isGround() {
        return false;
    }

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply(this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test(this);
    }

    @Override public String toString() {
        return "!" + getOp() + "(" + super.toString() + ")";
    }

    public static TermOp of(String op, ITerm... args) {
        return TermOp.of(op, Arrays.asList(args));
    }

}