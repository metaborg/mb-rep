package org.metaborg.unification.terms;

public final class ConsTerm extends TermWithArgs implements ITerm {

    private final int hashCode;

    public ConsTerm(ITerm head, IListTerm tail) {
        super(new ITerm[] { head, tail });
        this.hashCode = calcHashCode();
    }

    public ITerm getHead() {
        return getArgs()[0];
    }

    public IListTerm getTail() {
        return (IListTerm) getArgs()[1];
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
        return "[" + getHead().toString() + "|" + getTail().toString() + "]";
    }

}