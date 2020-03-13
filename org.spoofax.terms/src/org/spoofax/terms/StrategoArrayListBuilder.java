package org.spoofax.terms;

import java.util.Arrays;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Builds an IStrategoList by building up an internal array like ArrayList (doubling the size when we run out of
 * space) and then sharing that array
 */
public class StrategoArrayListBuilder implements IStrategoList.Builder {
    private IStrategoTerm[] array;
    private int index = 0;
    private boolean built = false;

    /**
     * Create an array-backed IStrategoList builder. Initializes the array with the given size.
     * @param size initial size of the backing array
     */
    public StrategoArrayListBuilder(int size) {
        this.array = new IStrategoTerm[size];
    }

    /**
     * Adds a term to the builder. If this terms doesn't fit in the backing array, the array is copied to an array
     * of twice the size.
     * @param term The term to add
     * @throws UnsupportedOperationException when one of the build methods was called on this builder previously
     * @see #build(), {@link #build(IStrategoList)}
     */
    public void add(IStrategoTerm term) {
        if(built) {
            throw new UnsupportedOperationException("Cannot add to a built list.");
        }
        if(index >= array.length) {
            if(array.length == 0) {
                array = new IStrategoTerm[2];
            } else {
                array = Arrays.copyOf(array, array.length * 2);
            }
        }
        array[index] = term;
        index++;
    }

    /**
     * This finalizes the internally accumulated terms and builds a List.
     * After calling this method, you can no longer {@link #add(IStrategoTerm)} to this builder.
     * @return The array backed IStrategoList
     */
    public IStrategoList build() {
        built = true;
        return new StrategoArrayList(array, null, 0, index);
    }

    @Override
    public boolean isEmpty() {
        return index == 0;
    }

    /**
     * This finalizes the internally accumulated terms and builds a List.
     * After calling this method, you can no longer {@link #add(IStrategoTerm)} to this builder.
     * @param annotations Annotations to put on the list that is built
     * @return The (annotated) array backed IStrategoList
     */
    public IStrategoList build(IStrategoList annotations) {
        built = true;
        return new StrategoArrayList(array, annotations, 0, index);
    }
}
