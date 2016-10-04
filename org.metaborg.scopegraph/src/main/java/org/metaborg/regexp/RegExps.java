package org.metaborg.regexp;

public final class RegExps {

    private RegExps() {
    }

    public static <S> IRegExp<S> emptySet() {
        return new EmptySet<>();
    }

    public static <S> IRegExp<S> emptyString() {
        return new EmptyString<>();
    }

    public static <S> IRegExp<S> symbol(S s) {
        return Symbol.of(s);

    }

    public static <S> IRegExp<S> concat(IRegExp<S> re1, IRegExp<S> re2) {
        return Concat.of(re1, re2);
    }

    public static <S> IRegExp<S> closure(IRegExp<S> re) {
        return Closure.of(re);
    }

    public static <S> IRegExp<S> and(IRegExp<S> re1, IRegExp<S> re2) {
        return And.of(re1, re2);
    }

    public static <S> IRegExp<S> or(IRegExp<S> re1, IRegExp<S> re2) {
        return Or.of(re1, re2);
    }

    public static <S> IRegExp<S> complement(IRegExp<S> re) {
        return Complement.of(re);
    }

}
