package org.spoofax.terms.util;

import jakarta.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Pretends to be a Set, but can also be asked to intern a string
 * or determine if a string has been interned already. This interner
 * does not keep strong references to the Strings in it, only weak ones.
 *
 * Based on {@see java.util.Collections.SetFromMap}.
 */
public final class StringInterner extends AbstractSet<String> implements Set<String> {
    // INVARIANT: key == value. The value is mapped in a WeakReference so it doesn't count as a strong ref. But if you
    // can access the entry, you can get that key out of the value. This can be used to properly intern strings,
    // returning the equivalent String object
    private final WeakHashMap<String, WeakReference<String>> map;
    // N.B. this is the keySet of the WeakHashMap and does not make strong references to the keys either.
    private final Set<String> mapKeys;

    /**
     * Initializes a new instance of the {@link StringInterner} class.
     */
    public StringInterner() {
        this.map = new WeakHashMap<>();
        this.mapKeys = map.keySet();
    }

    /**
     * Interns the specified string.
     *
     * @param e the string to intern
     * @return the interned string instance; or the existing interned string instance, if any
     */
    public String intern(String e) {
        @Nullable WeakReference<String> internedString = map.get(e);
        if(internedString != null) {
            // An interned value for the string is present in the map
            @Nullable String stringValue = internedString.get();
            if (stringValue != null) {
                // The interned value has not been garbage collected
                return stringValue;
            }
        }

        // No interned value for the string was present in the map,
        // or the interned value has been garbage collected
        map.put(e, new WeakReference<>(e));
        return e;
    }

    /**
     * Determines whether the specified string is the interned instance of the string.
     *
     * @param e the string to check
     * @return {@code true} when the string is the interned instance of the string;
     * otherwise, {@code false} when it is a different instance
     */
    public boolean isInterned(String e) {
        WeakReference<String> internedString = map.get(e);
        //noinspection StringEquality
        return internedString != null && internedString.get() == e;
    }

    @Override public void clear() {
        map.clear();
    }

    @Override public int size() {
        return map.size();
    }

    @Override public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override public boolean add(String e) {
        if (map.get(e) == null) {
            // Put a new mapping in the map
            map.put(e, new WeakReference<>(e));
            return true;
        } else {
            // Retain the existing mapping in the map
            return false;
        }
    }

    @Override public Iterator<String> iterator() {
        return mapKeys.iterator();
    }

    @Override public Object[] toArray() {
        return mapKeys.toArray();
    }

    @Override public <T> T[] toArray(T[] a) {
        return mapKeys.toArray(a);
    }

    @Override public String toString() {
        return mapKeys.toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringInterner)) return false;
        // Use the normal implementation for Set equals
        return super.equals(o);
    }

    @Override public int hashCode() {
        return mapKeys.hashCode();
    }

    @Override public boolean containsAll(Collection<?> c) {
        return mapKeys.containsAll(c);
    }

    @Override public boolean removeAll(Collection<?> c) {
        return mapKeys.removeAll(c);
    }

    @Override public boolean retainAll(Collection<?> c) {
        return mapKeys.retainAll(c);
    }

    @Override public boolean addAll(Collection<? extends String> c) {
        return super.addAll(c);
    }

    // Override default methods in Collection
    @Override public void forEach(Consumer<? super String> action) {
        mapKeys.forEach(action);
    }

    @Override public boolean removeIf(Predicate<? super String> filter) {
        return mapKeys.removeIf(filter);
    }

    @Override public Spliterator<String> spliterator() {
        return mapKeys.spliterator();
    }

    @Override public Stream<String> stream() {
        return mapKeys.stream();
    }

    @Override public Stream<String> parallelStream() {
        return mapKeys.parallelStream();
    }

}