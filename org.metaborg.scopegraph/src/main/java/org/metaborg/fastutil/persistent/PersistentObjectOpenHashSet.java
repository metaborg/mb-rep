package org.metaborg.fastutil.persistent;

public final class PersistentObjectOpenHashSet<T> implements PersistentObjectSet<T> {

    private Inner<T> inner;

    public PersistentObjectOpenHashSet() {
        this.inner = new Store<T>();
    }

    private PersistentObjectOpenHashSet(Inner<T> inner) {
        this.inner = inner;
    }

    @Override public boolean contains(T elem) {
        return reroot().store.contains(elem);
    }

    @Override public PersistentObjectSet<T> add(T elem) {
        return reroot().add(this, elem);
    }

    @Override public PersistentObjectSet<T> remove(T elem) {
        return reroot().remove(this, elem);
    }

    @Override public int size() {
        return reroot().store.size();
    }

    @Override public boolean isEmpty() {
        return reroot().store.isEmpty();
    }

    private Store<T> reroot() {
        return inner.reroot(this);
    }

    private interface Inner<T> {

        Store<T> reroot(PersistentObjectOpenHashSet<T> outer);
    }

    private static class Store<T> implements Inner<T> {

        private final it.unimi.dsi.fastutil.objects.ObjectSet<T> store = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<T>();

        public PersistentObjectSet<T> add(PersistentObjectOpenHashSet<T> outer, T elem) {
            PersistentObjectOpenHashSet<T> res = new PersistentObjectOpenHashSet<T>(this);
            if (store.contains(elem)) {
                return outer;
            } else {
                outer.inner = new Remove<>(elem, res);
            }
            store.add(elem);
            return res;
        }

        public PersistentObjectSet<T> remove(PersistentObjectOpenHashSet<T> outer, T elem) {
            if (!store.contains(elem)) {
                return outer;
            }
            PersistentObjectOpenHashSet<T> res = new PersistentObjectOpenHashSet<T>(this);
            outer.inner = new Add<>(elem, res);
            store.remove(elem);
            return res;
        }

        @Override public Store<T> reroot(PersistentObjectOpenHashSet<T> outer) {
            return this;
        };
    }

    private static class Add<T> implements Inner<T> {

        private final T elem;
        private final PersistentObjectOpenHashSet<T> next;

        public Add(T elem, PersistentObjectOpenHashSet<T> next) {
            this.elem = elem;
            this.next = next;
        }

        @Override public Store<T> reroot(PersistentObjectOpenHashSet<T> outer) {
            Store<T> store = next.reroot();
            assert !store.store.contains(elem);
            store.store.add(elem);
            outer.inner = store;
            next.inner = new Remove<>(elem, outer);
            return store;
        };

    }

    private static class Remove<T> implements Inner<T> {

        private final T elem;
        private final PersistentObjectOpenHashSet<T> next;

        public Remove(T elem, PersistentObjectOpenHashSet<T> next) {
            this.elem = elem;
            this.next = next;
        }

        @Override public Store<T> reroot(PersistentObjectOpenHashSet<T> outer) {
            Store<T> store = next.reroot();
            assert store.store.contains(elem);
            store.store.remove(elem);
            outer.inner = store;
            next.inner = new Add<>(elem, outer);
            return store;
        };

    }

}