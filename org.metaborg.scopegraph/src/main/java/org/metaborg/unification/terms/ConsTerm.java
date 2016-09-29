package org.metaborg.unification.terms;

public final class ConsTerm implements IListTerm {

    private static final long serialVersionUID = 1000527612219597398L;

    private final ITerm head;
    private final IListTerm tail;
    private final int hashCode;

    public ConsTerm(ITerm head, IListTerm tail) {
        this.head = head;
        this.tail = tail;
        this.hashCode = calcHashCode();
    }

    public ITerm getHead() {
        return head;
    }

    public IListTerm getTail() {
        return tail;
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
        result = prime * result + head.hashCode();
        result = prime * result + tail.hashCode();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        ConsTerm other = (ConsTerm) obj;
        if (!head.equals(other.head))
            return false;
        if (!tail.equals(other.tail))
            return false;
        return true;
    }

    @Override public String toString() {
        return "[" + head.toString() + "|" + tail.toString() + "]";
    }

}