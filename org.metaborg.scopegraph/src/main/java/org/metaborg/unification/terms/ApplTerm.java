package org.metaborg.unification.terms;

public final class ApplTerm extends TermWithArgs implements ITerm {

    private final String op;
    private final int hashCode;

    public ApplTerm(String op, ITerm... args) {
        super(args);
        this.op = op;
        this.hashCode = calcHashCode();
    }

    public String getOp() {
        return op;
    }

    @Override public <T> T accept(ITermVisitor<T> visitor) {
        return visitor.visit(this);
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + op.hashCode();
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
        ApplTerm other = (ApplTerm) obj;
        if (!op.equals(other.op))
            return false;
        if (!super.equals(obj))
            return false;
        return true;
    }

    @Override public String toString() {
        return op + "(" + super.toString() + ")";
    }

}
