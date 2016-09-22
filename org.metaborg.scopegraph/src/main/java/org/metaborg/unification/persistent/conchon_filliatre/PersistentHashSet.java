package org.metaborg.unification.persistent.conchon_filliatre;

import java.util.HashSet;

public final class PersistentHashSet<T> implements IPersistentSet<T> {
 
    private Inner<T> inner;
 
    public PersistentHashSet() {
        this.inner = new Store<T>();
    }
 
    private PersistentHashSet(Inner<T> inner) {
        this.inner = inner;
    }
 
    @Override public boolean contains(T elem) {
        return reroot().contains(elem);
    }

    @Override public PersistentHashSet<T> add(T elem) {
        return reroot().add(this,elem);
    }
 
    @Override public PersistentHashSet<T> remove(T elem) {
        return reroot().remove(this,elem);
    }
 
    private Store<T> reroot() {
        return inner.reroot(this);
    }
    
    
    private interface Inner<T> {
        Store<T> reroot(PersistentHashSet<T> outer);
    }
 
    private static class Store<T> implements Inner<T>  {
        private final HashSet<T> store = new HashSet<T>();
        
        public boolean contains(T elem) {
            return store.contains(elem);
        }

        public PersistentHashSet<T> add(PersistentHashSet<T> outer, T elem) {
            PersistentHashSet<T> res = new PersistentHashSet<T>(this);
            if(store.contains(elem)) {
                return outer;
            } else {
                outer.inner = new Remove<>(elem, res);
            }
            store.add(elem);
            return res;
        }
 
        public PersistentHashSet<T> remove(PersistentHashSet<T> outer, T elem) {
            if(!store.contains(elem)) {
                return outer;
            }
            PersistentHashSet<T> res = new PersistentHashSet<T>(this);
            outer.inner = new Add<>(elem, res);
            store.remove(elem);
            return res;
        }
        
        @Override public Store<T> reroot(PersistentHashSet<T> outer) {
            return this;
        };
    }

    private static class Add<T> implements Inner<T> {
        private final T elem;
        private final PersistentHashSet<T> next;

        public Add(T elem, PersistentHashSet<T> next) {
            this.elem = elem;
            this.next = next;
        }

        @Override public Store<T> reroot(PersistentHashSet<T> outer) {
            Store<T> store = next.reroot();
            assert !store.contains(elem);
            store.store.add(elem);
            outer.inner = store;
            next.inner = new Remove<>(elem, outer);
            return store;
        };
    }

    private static class Remove<T> implements Inner<T> {
        private final T elem;
        private final PersistentHashSet<T> next;

        public Remove(T elem, PersistentHashSet<T> next) {
            this.elem = elem;
            this.next = next;
        }

        @Override public Store<T> reroot(PersistentHashSet<T> outer) {
            Store<T> store = next.reroot();
            assert store.contains(elem);
            store.store.remove(elem);
            outer.inner = store;
            next.inner = new Add<>(elem, outer);
            return store;
        };
    }

}