package org.metaborg.unification.terms;

public final class DoubleTerm implements IPrimitiveTerm {

    private final double value;
    private final int hashCode;

    public DoubleTerm(double value) {
        this.value = value;
        this.hashCode = calcHashCode();
    }

    public double getValue() {
        return value;
    }

    @Override public <T> T accept(ITermVisitor<T> visitor) {
        return visitor.visit(this);
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