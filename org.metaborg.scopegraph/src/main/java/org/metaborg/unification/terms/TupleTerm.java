package org.metaborg.unification.terms;

public final class TupleTerm extends TermWithArgs implements ITerm {

    private final int hashCode;

    public TupleTerm(ITerm... args) {
        super(args);
        this.hashCode = calcHashCode();
    }

    @Override public <T> T accept(ITermVisitor<T> visitor) {
        return visitor.visit(this);
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
