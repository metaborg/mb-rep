package org.metaborg.fastutil.persistent;

import java.util.Set;

public interface PersistentObject2ObjectMap<K, V> {

    boolean containsKey(K key);

    V get(K key);

    PersistentObject2ObjectMap<K,V> put(K key, V value);

    PersistentObject2ObjectMap<K,V> remove(K key);

    Set<K> keySet();

}