package org.metaborg.fastutil.persistent;

public interface ObjectPSet<T> extends Iterable<T> {

    boolean contains(T elem);

    ObjectPSet<T> add(T elem);

    ObjectPSet<T> remove(T elem);

    boolean isEmpty();

    int size();

}