package org.spoofax.terms.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility methods for working with iterables.
 */
public final class IterableUtils {

    /**
     * Creates an array from the specified iterable.
     *
     * @param iterable the iterable
     * @param array an array into which the elements are stored;
     *              or from which a new array of the same type is created
     * @param <T> the type of elements
     * @return the resulting array
     */
    public static <T> T[] toArray(Iterable<T> iterable, T[] array) {
        return toCollection(iterable).toArray(array);
    }

    /**
     * Creates a collection from the specified iterable, if it is not already.
     *
     * @param iterable the iterable
     * @param <T> the type of elements
     * @return the resulting collection
     */
    public static <T> Collection<T> toCollection(Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            return (Collection<T>)iterable;
        }
        ArrayList<T> list = new ArrayList<>();
        for (T element : iterable) {
            list.add(element);
        }
        return list;
    }

}
