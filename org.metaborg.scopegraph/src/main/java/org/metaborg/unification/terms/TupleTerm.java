package org.metaborg.unification.terms;

import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

import com.google.common.collect.ImmutableList;

public final class TupleTerm extends TermWithArgs implements ITerm {

    private static final long serialVersionUID = -3435889275904515538L;

    private final int hashCode;

    public TupleTerm(ITerm... args) {
        this(ImmutableList.copyOf(args));
    }

    public TupleTerm(ImmutableList<ITerm> args) {
        super(args);
        this.hashCode = calcHashCode();
    }

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply(this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test(this);
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        return result;
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;
        return true;
    }

    @Override public String toString() {
        return "(" + super.toString() + ")";
    }

}
