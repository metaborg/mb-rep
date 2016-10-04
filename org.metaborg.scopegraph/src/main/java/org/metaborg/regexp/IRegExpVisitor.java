package org.metaborg.regexp;

public interface IRegExpVisitor<S, T> {

    T symbol(S symbol);

    T or(IRegExp<S> left, IRegExp<S> right);

    T and(IRegExp<S> left, IRegExp<S> right);

    T concat(IRegExp<S> left, IRegExp<S> right);

    T complement(IRegExp<S> re);

    T closure(IRegExp<S> re);

    T emptyString();

    T emptySet();

}