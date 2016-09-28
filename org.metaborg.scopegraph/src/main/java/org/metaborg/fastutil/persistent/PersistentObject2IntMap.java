package org.metaborg.fastutil.persistent;

public interface PersistentObject2IntMap<K> {

    boolean containsKey(K key);

    int get(K key);

    PersistentObject2IntMap<K> put(K key, int value);

    PersistentObject2IntMap<K> remove(K key);

}