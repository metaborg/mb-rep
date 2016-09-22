package org.metaborg.unification.persistent.conchon_filliatre;

import java.util.HashMap;

public final class PersistentHashMap<K,V> implements IPersistentMap<K,V> {
 
    private Inner<K,V> inner;
 
    public PersistentHashMap() {
        this.inner = new Store<K,V>();
    }
 
    private PersistentHashMap(Inner<K,V> inner) {
        this.inner = inner;
    }
 
    @Override public boolean containsKey(K key) {
        return reroot().containsKey(key);
    }

    @Override public V get(K key) {
        return reroot().get(key);
    }

    @Override public PersistentHashMap<K,V> put(K key, V value) {
        return reroot().put(this,key,value);
    }
 
    @Override public PersistentHashMap<K,V> remove(K key) {
        return reroot().remove(this,key);
    }
 
    private Store<K,V> reroot() {
        return inner.reroot(this);
    }
    
    
    private interface Inner<K,V> {
        Store<K,V> reroot(PersistentHashMap<K,V> outer);
    }
 
    private static class Store<K,V> implements Inner<K,V>  {
        private final HashMap<K,V> store = new HashMap<K,V>();
        
        public boolean containsKey(K key) {
            return store.containsKey(key);
        }

        public V get(K key) {
            return store.get(key);
        }

        public PersistentHashMap<K,V> put(PersistentHashMap<K,V> outer, K key, V value) {
            PersistentHashMap<K,V> res = new PersistentHashMap<K,V>(this);
            if(store.containsKey(key)) {
                V oldValue = store.get(key);
                outer.inner = new Update<>(key, oldValue, res);
            } else {
                outer.inner = new Remove<>(key, res);
            }
            store.put(key, value);
            return res;
        }
 
        public PersistentHashMap<K,V> remove(PersistentHashMap<K,V> outer, K key) {
            if(!store.containsKey(key)) {
                return outer;
            }
            PersistentHashMap<K,V> res = new PersistentHashMap<K,V>(this);
            V oldValue = store.get(key);
            outer.inner = new Add<>(key, oldValue, res);
            store.remove(key);
            return res;
        }
        
        @Override public Store<K,V> reroot(PersistentHashMap<K,V> outer) {
            return this;
        };
    }

    private static class Add<K,V> implements Inner<K,V> {
        private final K key;
        private final V value;
        private final PersistentHashMap<K,V> next;

        public Add(K key, V value, PersistentHashMap<K,V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override public Store<K,V> reroot(PersistentHashMap<K,V> outer) {
            Store<K,V> store = next.reroot();
            assert !store.containsKey(key);
            store.store.put(key, value);
            outer.inner = store;
            next.inner = new Remove<>(key, outer);
            return store;
        };
    }

    private static class Update<K,V> implements Inner<K,V> {
        private final K key;
        private final V value;
        private final PersistentHashMap<K,V> next;

        public Update(K key, V value, PersistentHashMap<K,V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override public Store<K,V> reroot(PersistentHashMap<K,V> outer) {
            Store<K,V> store = next.reroot();
            assert store.containsKey(key);
            V oldValue = store.store.get(key);
            store.store.put(key, value);
            outer.inner = store;
            next.inner = new Update<>(key, oldValue, outer);
            return store;
        };
    }

    private static class Remove<K,V> implements Inner<K,V> {
        private final K key;
        private final PersistentHashMap<K,V> next;

        public Remove(K key, PersistentHashMap<K,V> next) {
            this.key = key;
            this.next = next;
        }

        @Override public Store<K,V> reroot(PersistentHashMap<K,V> outer) {
            Store<K,V> store = next.reroot();
            assert store.containsKey(key);
            V oldValue = store.store.get(key);
            store.store.remove(key);
            outer.inner = store;
            next.inner = new Add<>(key, oldValue, outer);
            return store;
        };
    }

}