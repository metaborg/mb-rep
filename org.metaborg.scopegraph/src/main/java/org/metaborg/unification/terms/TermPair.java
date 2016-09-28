package org.metaborg.unification.terms;

public final class TermPair {

    public final ITerm first;
    public final ITerm second;

    private TermPair(ITerm first, ITerm second) {
        this.first = first;
        this.second = second;
    }

    public static TermPair of(ITerm first, ITerm second) {
        return new TermPair(first, second);
    }

    @Override public String toString() {
        return "(" + first.toString() + "," + second.toString() + ")";
    }

}
