package org.metaborg.fastutil.persistent;

public final class PersistentObject2IntOpenHashMap<K> implements PersistentObject2IntMap<K> {

    private Inner<K> inner;

    public PersistentObject2IntOpenHashMap() {
        this.inner = new Store<K>();
    }

    private PersistentObject2IntOpenHashMap(Inner<K> inner) {
        this.inner = inner;
    }

    @Override public boolean containsKey(K key) {
        return reroot().containsKey(key);
    }

    @Override public int get(K key) {
        return reroot().get(key);
    }

    @Override public PersistentObject2IntMap<K> put(K key, int value) {
        return reroot().put(this, key, value);
    }

    @Override public PersistentObject2IntMap<K> remove(K key) {
        return reroot().remove(this, key);
    }

    private Store<K> reroot() {
        return inner.reroot(this);
    }


    private interface Inner<K> {

        Store<K> reroot(PersistentObject2IntOpenHashMap<K> outer);
    }

    private static class Store<K> implements Inner<K> {

        private final it.unimi.dsi.fastutil.objects.Object2IntMap<K> store = new it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<K>();

        public boolean containsKey(K key) {
            return store.containsKey(key);
        }

        public int get(K key) {
            return store.get(key);
        }

        public PersistentObject2IntMap<K> put(PersistentObject2IntOpenHashMap<K> outer, K key, int value) {
            PersistentObject2IntOpenHashMap<K> res = new PersistentObject2IntOpenHashMap<K>(this);
            if (store.containsKey(key)) {
                int oldValue = store.get(key);
                outer.inner = new Update<>(key, oldValue, res);
            } else {
                outer.inner = new Remove<>(key, res);
            }
            store.put(key, value);
            return res;
        }

        public PersistentObject2IntMap<K> remove(PersistentObject2IntOpenHashMap<K> outer, K key) {
            if (!store.containsKey(key)) {
                return outer;
            }
            PersistentObject2IntOpenHashMap<K> res = new PersistentObject2IntOpenHashMap<K>(this);
            int oldValue = store.get(key);
            outer.inner = new Add<>(key, oldValue, res);
            store.remove(key);
            return res;
        }

        @Override public Store<K> reroot(PersistentObject2IntOpenHashMap<K> outer) {
            return this;
        };
    }

    private static class Add<K> implements Inner<K> {

        private final K key;
        private final int value;
        private final PersistentObject2IntOpenHashMap<K> next;

        public Add(K key, int value, PersistentObject2IntOpenHashMap<K> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override public Store<K> reroot(PersistentObject2IntOpenHashMap<K> outer) {
            Store<K> store = next.reroot();
            assert !store.containsKey(key);
            store.store.put(key, value);
            outer.inner = store;
            next.inner = new Remove<>(key, outer);
            return store;
        };
    }

    private static class Update<K> implements Inner<K> {

        private final K key;
        private final int value;
        private final PersistentObject2IntOpenHashMap<K> next;

        public Update(K key, int value, PersistentObject2IntOpenHashMap<K> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override public Store<K> reroot(PersistentObject2IntOpenHashMap<K> outer) {
            Store<K> store = next.reroot();
            assert store.containsKey(key);
            int oldValue = store.store.get(key);
            store.store.put(key, value);
            outer.inner = store;
            next.inner = new Update<>(key, oldValue, outer);
            return store;
        };
    }

    private static class Remove<K> implements Inner<K> {

        private final K key;
        private final PersistentObject2IntOpenHashMap<K> next;

        public Remove(K key, PersistentObject2IntOpenHashMap<K> next) {
            this.key = key;
            this.next = next;
        }

        @Override public Store<K> reroot(PersistentObject2IntOpenHashMap<K> outer) {
            Store<K> store = next.reroot();
            assert store.containsKey(key);
            int oldValue = store.store.get(key);
            store.store.remove(key);
            outer.inner = store;
            next.inner = new Add<>(key, oldValue, outer);
            return store;
        };
    }

}