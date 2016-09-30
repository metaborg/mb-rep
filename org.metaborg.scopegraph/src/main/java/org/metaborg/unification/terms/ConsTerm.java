package org.metaborg.unification.terms;

import org.metaborg.unification.IListTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

public final class ConsTerm implements IListTerm {

    private static final long serialVersionUID = 1000527612219597398L;

    private final ITerm head;
    private final IListTerm tail;
    private final boolean isGround;
    private final int hashCode;

    public ConsTerm(ITerm head, IListTerm tail) {
        this.head = head;
        this.tail = tail;
        this.isGround = head.isGround() && tail.isGround();
        this.hashCode = calcHashCode();
    }

    public ITerm getHead() {
        return head;
    }

    public IListTerm getTail() {
        return tail;
    }

    @Override public boolean isGround() {
        return isGround;
    }

    @Override public <T> T apply(ITermFunction<T> visitor) {
        return visitor.apply(this);
    }

    @Override public boolean test(ITermPredicate predicate) {
        return predicate.test(this);
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