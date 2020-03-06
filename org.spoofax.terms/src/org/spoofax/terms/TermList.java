package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.StreamSupport;

/**
 * An immutable list of terms.
 */
public final class TermList extends AbstractList<IStrategoTerm> implements RandomAccess, Serializable {

    private static final TermList EMPTY = new TermList(TermFactory.EMPTY_TERM_ARRAY);

    /**
     * Creates a new term list from the specified list of terms.
     *
     * @param terms the terms in the list
     * @return the created list; or an empty list
     */
    public static TermList fromIterable(Iterable<IStrategoTerm> terms) {
        // When the instance is already a TermList, we can reuse that since we guarantee the contents cannot be modified.
        if (terms instanceof TermList) return (TermList)terms;
        if (terms instanceof Collection<?>) {
            Collection<IStrategoTerm> collection = (Collection<IStrategoTerm>)terms;
            if (collection.size() == 0) return EMPTY;
            IStrategoTerm[] array = collection.toArray(new IStrategoTerm[0]);
            return new TermList(array);
        } else {
            IStrategoTerm[] array = StreamSupport.stream(terms.spliterator(), false).toArray(IStrategoTerm[]::new);
            if (array.length == 0) return EMPTY;
            return new TermList(array);
        }
    }

    /**
     * Creates a new term list from the specified array of terms.
     *
     * @param terms the terms in the list
     * @return the created list; or an empty list
     */
    public static TermList of(IStrategoTerm... terms) {
        if (terms.length == 0) return EMPTY;
        IStrategoTerm[] array = terms.clone();
        return new TermList(array);
    }

    /**
     * Creates a new term list from the specified array of terms,
     * without copying the array.
     *
     * Note: call this function only on arrays that you're sure will not be modified.
     *
     * @param terms the terms in the list
     * @return the created list; or an empty list
     */
    public static TermList ofUnsafe(IStrategoTerm... terms) {
        if (terms.length == 0) return EMPTY;
        return new TermList(terms);
    }

    /**
     * Creates an empty term list.
     *
     * @return the empty list
     */
    public static TermList of() {
        return EMPTY;
    }

    private final IStrategoTerm[] array;

    /**
     * Initializes a new instance of the {@link TermList} class.
     *
     * Note: the array must not be modifiable outside of this class
     * (and will not be modified inside this class). I.e., this class
     * must be the sole owner of the array.
     *
     * @param array the array being wrapped
     */
    private TermList(IStrategoTerm[] array) {
        this.array = array;
    }

    @Override
    public IStrategoTerm get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    // Unsupported methods

//    @Override
//    @Deprecated
//    public boolean add(IStrategoTerm iStrategoTerm) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public void add(int index, IStrategoTerm element) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public boolean addAll(Collection<? extends IStrategoTerm> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public boolean addAll(int index, Collection<? extends IStrategoTerm> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    protected void removeRange(int fromIndex, int toIndex) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public boolean remove(Object o) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public boolean removeAll(Collection<?> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public boolean removeIf(Predicate<? super IStrategoTerm> filter) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public IStrategoTerm remove(int index) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public void clear() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public void replaceAll(UnaryOperator<IStrategoTerm> operator) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public boolean retainAll(Collection<?> c) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public IStrategoTerm set(int index, IStrategoTerm element) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    @Deprecated
//    public void sort(Comparator<? super IStrategoTerm> c) {
//        throw new UnsupportedOperationException();
//    }
}
