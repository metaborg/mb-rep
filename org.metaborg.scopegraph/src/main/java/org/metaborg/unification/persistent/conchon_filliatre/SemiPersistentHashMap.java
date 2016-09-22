package org.metaborg.unification.persistent.conchon_filliatre;

import java.util.HashMap;

public final class SemiPersistentHashMap<K,V> implements IPersistentMap<K,V> {
 
    private Inner<K,V> inner;
 
    public SemiPersistentHashMap() {
        this.inner = new Store<K,V>();
    }
 
    private SemiPersistentHashMap(Inner<K,V> inner) {
        this.inner = inner;
    }
 
    @Override public boolean containsKey(K key) {
        return reroot().containsKey(key);
    }

    @Override public V get(K key) {
        return reroot().get(key);
    }

    @Override public SemiPersistentHashMap<K,V> put(K key, V value) {
        return reroot().put(this,key,value);
    }
 
    @Override public SemiPersistentHashMap<K,V> remove(K key) {
        return reroot().remove(this,key);
    }
 
    private Store<K,V> reroot() {
        return inner.reroot(this);
    }
    
    
    private interface Inner<K,V> {
        Store<K,V> reroot(SemiPersistentHashMap<K,V> outer);
    }
 
    private static class Store<K,V> implements Inner<K,V>  {
        private final HashMap<K,V> store = new HashMap<K,V>();
        
        public boolean containsKey(K key) {
            return store.containsKey(key);
        }

        public V get(K key) {
            return store.get(key);
        }

        public SemiPersistentHashMap<K,V> put(SemiPersistentHashMap<K,V> outer, K key, V value) {
            SemiPersistentHashMap<K,V> res = new SemiPersistentHashMap<K,V>(this);
            if(store.containsKey(key)) {
                V oldValue = store.get(key);
                outer.inner = new Update<>(key, oldValue, res);
            } else {
                outer.inner = new Remove<>(key, res);
            }
            store.put(key, value);
            return res;
        }
 
        public SemiPersistentHashMap<K,V> remove(SemiPersistentHashMap<K,V> outer, K key) {
            if(!store.containsKey(key)) {
                return outer;
            }
            SemiPersistentHashMap<K,V> res = new SemiPersistentHashMap<K,V>(this);
            V oldValue = store.get(key);
            outer.inner = new Add<>(key, oldValue, res);
            store.remove(key);
            return res;
        }
        
        @Override public Store<K,V> reroot(SemiPersistentHashMap<K,V> outer) {
            return this;
        };
    }

    private static class Add<K,V> implements Inner<K,V> {
        private final K key;
        private final V value;
        private final SemiPersistentHashMap<K,V> next;

        public Add(K key, V value, SemiPersistentHashMap<K,V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override public Store<K,V> reroot(SemiPersistentHashMap<K,V> outer) {
            Store<K,V> store = next.reroot();
            assert !store.containsKey(key);
            store.store.put(key, value);
            outer.inner = store;
            next.inner = new Invalid<>();
            return store;
        };
    }

    private static class Update<K,V> implements Inner<K,V> {
        private final K key;
        private final V value;
        private final SemiPersistentHashMap<K,V> next;

        public Update(K key, V value, SemiPersistentHashMap<K,V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override public Store<K,V> reroot(SemiPersistentHashMap<K,V> outer) {
            Store<K,V> store = next.reroot();
            assert store.containsKey(key);
            store.store.put(key, value);
            outer.inner = store;
            next.inner = new Invalid<>();
            return store;
        };
    }

    private static class Remove<K,V> implements Inner<K,V> {
        private final K key;
        private final SemiPersistentHashMap<K,V> next;

        public Remove(K key, SemiPersistentHashMap<K,V> next) {
            this.key = key;
            this.next = next;
        }

        @Override public Store<K,V> reroot(SemiPersistentHashMap<K,V> outer) {
            Store<K,V> store = next.reroot();
            assert store.containsKey(key);
            store.store.remove(key);
            outer.inner = store;
            next.inner = new Invalid<>();
            return store;
        };
    }

    private static class Invalid<K,V> implements Inner<K,V> {
        @Override public Store<K,V> reroot(SemiPersistentHashMap<K,V> outer) {
            throw new IllegalStateException("Map accessed after backtracking.");
        }
    }
    
    
}