package org.metaborg.unification.terms;

import org.metaborg.unification.IListTerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

public final class NilTerm implements IListTerm {

    private static final long serialVersionUID = 1000527612219597398L;

    public NilTerm() {
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

    @Override public int hashCode() {
        return 1;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

    @Override public String toString() {
        return "[]";
    }

}