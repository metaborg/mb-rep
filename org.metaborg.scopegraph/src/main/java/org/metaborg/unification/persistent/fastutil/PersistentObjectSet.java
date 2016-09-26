package org.metaborg.unification.persistent.fastutil;

public interface PersistentObjectSet<T> {

    boolean contains(T elem);

    PersistentObjectSet<T> add(T elem);

    PersistentObjectSet<T> remove(T elem);

}