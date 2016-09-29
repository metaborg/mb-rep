package org.metaborg.unification.terms;

import com.google.common.collect.ImmutableList;

public final class TermOp extends TermWithArgs implements IAny {

    private static final long serialVersionUID = -1580659198005175545L;

    private final String op;
    private final int hashCode;

    public TermOp(String op, ITerm... args) {
        this(op, ImmutableList.copyOf(args));
    }

    public TermOp(String op, ImmutableList<ITerm> args) {
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
        TermOp other = (TermOp) obj;
        if (!op.equals(other.op))
            return false;
        if (!super.equals(obj))
            return false;
        return true;
    }

    @Override public String toString() {
        return "!" + op + "(" + super.toString() + ")";
    }

}