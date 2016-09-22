package org.metaborg.unification.persistent.conchon_filliatre;

import java.util.HashSet;

public final class SemiPersistentHashSet<T> implements IPersistentSet<T> {
 
    private Inner<T> inner;
 
    public SemiPersistentHashSet() {
        this.inner = new Store<T>();
    }
 
    private SemiPersistentHashSet(Inner<T> inner) {
        this.inner = inner;
    }
 
    @Override public boolean contains(T elem) {
        return reroot().contains(elem);
    }

    @Override public SemiPersistentHashSet<T> add(T elem) {
        return reroot().add(this,elem);
    }
 
    @Override public SemiPersistentHashSet<T> remove(T elem) {
        return reroot().remove(this,elem);
    }
 
    private Store<T> reroot() {
        return inner.reroot(this);
    }
    
    
    private interface Inner<T> {
        Store<T> reroot(SemiPersistentHashSet<T> outer);
    }
 
    private static class Store<T> implements Inner<T>  {
        private final HashSet<T> store = new HashSet<T>();
        
        public boolean contains(T elem) {
            return store.contains(elem);
        }

        public SemiPersistentHashSet<T> add(SemiPersistentHashSet<T> outer, T elem) {
            SemiPersistentHashSet<T> res = new SemiPersistentHashSet<T>(this);
            if(store.contains(elem)) {
                return outer;
            } else {
                outer.inner = new Remove<>(elem, res);
            }
            store.add(elem);
            return res;
        }
 
        public SemiPersistentHashSet<T> remove(SemiPersistentHashSet<T> outer, T elem) {
            if(!store.contains(elem)) {
                return outer;
            }
            SemiPersistentHashSet<T> res = new SemiPersistentHashSet<T>(this);
            outer.inner = new Add<>(elem, res);
            store.remove(elem);
            return res;
        }
        
        @Override public Store<T> reroot(SemiPersistentHashSet<T> outer) {
            return this;
        };
    }

    private static class Add<T> implements Inner<T> {
        private final T elem;
        private final SemiPersistentHashSet<T> next;

        public Add(T elem, SemiPersistentHashSet<T> next) {
            this.elem = elem;
            this.next = next;
        }

        @Override public Store<T> reroot(SemiPersistentHashSet<T> outer) {
            Store<T> store = next.reroot();
            assert !store.contains(elem);
            store.store.add(elem);
            outer.inner = store;
            next.inner = new Invalid<>();
            return store;
        };
    }

    private static class Remove<T> implements Inner<T> {
        private final T elem;
        private final SemiPersistentHashSet<T> next;

        public Remove(T elem, SemiPersistentHashSet<T> next) {
            this.elem = elem;
            this.next = next;
        }

        @Override public Store<T> reroot(SemiPersistentHashSet<T> outer) {
            Store<T> store = next.reroot();
            assert store.contains(elem);
            store.store.remove(elem);
            outer.inner = store;
            next.inner = new Invalid<>();
            return store;
        };
    }

    private static class Invalid<T> implements Inner<T> {
        @Override public Store<T> reroot(SemiPersistentHashSet<T> outer) {
            throw new IllegalStateException("Map accessed after backtracking.");
        }
    }
    
}