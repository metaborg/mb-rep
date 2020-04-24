package org.spoofax.terms.util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Based on {@see java.util.Collections.SetFromMap}. Pretends to be a HashSet, but can also be asked to intern a string
 * or see if a string is interned already.
 */
public class StringInterner extends AbstractSet<String> implements Set<String>, Serializable {
    // INVARIANT: key == value. The value is mapped in a WeakReference so it doesn't count as a strong ref. But if you
    // can access the entry, you can get that key out of the value. This can be used to properly intern strings,
    // returning the equivalent String object
    private final Map<String, WeakReference<String>> m;
    private transient Set<String> s;

    public StringInterner() {
        m = new WeakHashMap<>();
        s = m.keySet();
    }

    /**
     * Interns the specified string.
     *
     * @param e the string to intern
     * @return the interned string instance; or the existing interned string instance, if any
     */
    public String intern(String e) {
        WeakReference<String> internedString = m.get(e);
        if(internedString != null) {
            return internedString.get();
        } else {
            add(e);
            return e;
        }
    }

    /**
     * Determines whether the specified string is the interned instance of the string.
     *
     * @param e the string to check
     * @return {@code true} when the string is the interned instance of the string;
     * otherwise, {@code false} when it is a different instance
     */
    public boolean isInterned(String e) {
        WeakReference<String> internedString = m.get(e);
        //noinspection StringEquality
        return internedString != null && internedString.get() == e;
    }

    public void clear() {
        m.clear();
    }

    public int size() {
        return m.size();
    }

    public boolean isEmpty() {
        return m.isEmpty();
    }

    public boolean contains(Object o) {
        return m.containsKey(o);
    }

    public boolean remove(Object o) {
        return m.remove(o) != null;
    }

    public boolean add(String e) {
        return m.put(e, new WeakReference<>(e)) == null;
    }

    public Iterator<String> iterator() {
        return s.iterator();
    }

    public Object[] toArray() {
        return s.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return s.toArray(a);
    }

    public String toString() {
        return s.toString();
    }

    public int hashCode() {
        return s.hashCode();
    }

    public boolean equals(Object o) {
        return o == this || s.equals(o);
    }

    public boolean containsAll(Collection<?> c) {
        return s.containsAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return s.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return s.retainAll(c);
    }
    // addAll is the inherited

    // Override default methods in Collection
    @Override public void forEach(Consumer<? super String> action) {
        s.forEach(action);
    }

    @Override public boolean removeIf(Predicate<? super String> filter) {
        return s.removeIf(filter);
    }

    @Override public Spliterator<String> spliterator() {
        return s.spliterator();
    }

    @Override public Stream<String> stream() {
        return s.stream();
    }

    @Override public Stream<String> parallelStream() {
        return s.parallelStream();
    }

    private static final long serialVersionUID = 2454657854757543876L;

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        s = m.keySet();
    }
}