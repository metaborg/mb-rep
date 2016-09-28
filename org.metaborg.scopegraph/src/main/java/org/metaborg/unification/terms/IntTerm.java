package org.metaborg.unification.terms;

public final class IntTerm implements IPrimitiveTerm {

    private final int value;
    private final int hashCode;

    public IntTerm(int value) {
        this.value = value;
        this.hashCode = calcHashCode();
    }

    public int getValue() {
        return value;
    }

    @Override public <T> T accept(ITermVisitor<T> visitor) {
        return visitor.visit(this);
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
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
        IntTerm other = (IntTerm) obj;
        if (value != other.value)
            return false;
        return true;
    }

    @Override public java.lang.String toString() {
        return Integer.toString(value);
    }

}