package org.metaborg.unification.terms;

import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

public final class DoubleTerm implements IPrimitiveTerm {

    private static final long serialVersionUID = 6618120393430830314L;

    private final double value;
    private final int hashCode;

    public DoubleTerm(double value) {
        this.value = value;
        this.hashCode = calcHashCode();
    }

    public double getValue() {
        return value;
    }

    @Override public boolean isGround() {
        return true;
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
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DoubleTerm other = (DoubleTerm) obj;
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
            return false;
        return true;
    }

    @Override public java.lang.String toString() {
        return Double.toString(value);
    }

}