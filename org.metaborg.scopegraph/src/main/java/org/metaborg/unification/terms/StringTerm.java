package org.metaborg.unification.terms;

import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermPredicate;

public final class StringTerm implements IPrimitiveTerm {

    private static final long serialVersionUID = -5894215163299613968L;

    private final String value;
    private final int hashCode;

    public StringTerm(String value) {
        this.value = value;
        this.hashCode = calcHashCode();
    }

    public String getValue() {
        return value;
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

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value.hashCode();
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
        StringTerm other = (StringTerm) obj;
        if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override public String toString() {
        return "\"" + value + "\"";
    }

}
