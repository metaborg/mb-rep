package org.metaborg.fastutil.persistent;

public interface PersistentObjectSet<T> {

    boolean contains(T elem);

    PersistentObjectSet<T> add(T elem);

    PersistentObjectSet<T> remove(T elem);

    boolean isEmpty();

    int size();

}