package org.metaborg.fastutil.persistent;

public interface Object2IntPMap<K> {

    boolean containsKey(K key);

    int get(K key);

    Object2IntPMap<K> put(K key, int value);

    Object2IntPMap<K> remove(K key);

}