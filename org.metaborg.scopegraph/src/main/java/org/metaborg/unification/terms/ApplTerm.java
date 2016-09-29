package org.metaborg.unification.terms;

import com.google.common.collect.ImmutableList;

public final class ApplTerm extends TermWithArgs implements ITerm {

    private static final long serialVersionUID = 1425384232895407260L;

    private final String op;
    private final int hashCode;

    public ApplTerm(String op, ITerm... args) {
        this(op, ImmutableList.copyOf(args));
    }

    public ApplTerm(String op, ImmutableList<ITerm> args) {
        super(args);
        this.op = op;
        this.hashCode = calcHashCode();
    }

    public String getOp() {
        return op;
    }

    public int getArity() {
        return getArgs().size();
    }

    @Override public <T> T accept(ITermVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public int hashCode() {
        return hashCode;
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + op.hashCode();
        result = prime * result + super.hashCode();
        return result;
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