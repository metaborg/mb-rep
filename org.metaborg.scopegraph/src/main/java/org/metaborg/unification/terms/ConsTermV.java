package org.metaborg.unification.terms;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.metaborg.annotations.ConstructorClass;
import org.metaborg.unification.IListTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

@Value.Immutable
@ConstructorClass
@Serial.Structural
@SuppressWarnings("serial")
public abstract class ConsTermV implements IConsTerm {

    @Override
    public abstract ITerm getHead();

    @Override
    public abstract IListTerm getTail();

    @Value.Lazy @Override public boolean isGround() {
        return getHead().isGround() && getTail().isGround();
    }

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply(this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test(this);
    }

    @Override public String toString() {
        return "[" + getHead().toString() + "|" + getTail().toString() + "]";
    }

}