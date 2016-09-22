package org.metaborg.unification.persistent.conchon_filliatre;

public interface IPersistentMap<K,V> {
    boolean containsKey(K key);
    V get(K key);
    IPersistentMap<K,V> put(K key, V value);
    IPersistentMap<K,V> remove(K key);
}