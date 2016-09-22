package org.metaborg.unification.persistent.conchon_filliatre;

public interface IPersistentSet<T> {
    boolean contains(T elem);
    IPersistentSet<T> add(T elem);
    IPersistentSet<T> remove(T elem);
}