package org.metaborg.regexp;


public interface IAlphabet<S> extends Iterable<S> {

    boolean contains(S s);

    int indexOf(S s);

}
