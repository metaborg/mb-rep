package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.TermUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import static org.spoofax.terms.AbstractTermFactory.EMPTY_TERM_ARRAY;

public class StrategoArrayList extends StrategoTerm implements IStrategoList, RandomAccess {
    private final IStrategoTerm[] terms;
    private final int offset;
    private final int subtermCount;

    public StrategoArrayList(IStrategoTerm... terms) {
        this(terms, null, 0);
    }

    public StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations) {
        this(terms, annotations, 0);
    }

    protected StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations, int offset) {
        this(terms, annotations, offset, terms.length);
    }

    protected StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations, int offset, int endOffset) {
        super(annotations);
        if(offset > terms.length) {
            throw new IllegalArgumentException(
                "Offset (" + offset + ") is larger than backing array (" + terms.length + ")");
        }
        if(endOffset > terms.length) {
            throw new IllegalArgumentException(
                "Length (" + endOffset + ") is larger than backing array (" + terms.length + ")");
        }
        this.terms = terms;
        this.offset = offset;
        this.subtermCount = endOffset - offset;
    }

    public static StrategoArrayList fromCollection(Collection<? extends IStrategoTerm> terms) {
        return new StrategoArrayList(terms.toArray(EMPTY_TERM_ARRAY));
    }

    public static ArrayListBuilder arrayListBuilder() {
        return new ArrayListBuilder(16);
    }

    public static ArrayListBuilder arrayListBuilder(int size) {
        return new ArrayListBuilder(size);
    }

    @Override public int getSubtermCount() {
        return subtermCount;
    }

    @Override public IStrategoTerm getSubterm(int index) {
        if(index <= subtermCount) {
            return terms[offset + index];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return Arrays.copyOfRange(terms, offset, offset + subtermCount);
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(getAllSubterms());
    }

    @Override public int getTermType() {
        return IStrategoTerm.LIST;
    }

    @Deprecated @Override public void prettyPrint(ITermPrinter pp) {
        if(!isEmpty()) {
            pp.println("[");
            pp.indent(2);
            Iterator<IStrategoTerm> iter = iterator();
            iter.next().prettyPrint(pp);
            while(iter.hasNext()) {
                IStrategoTerm element = iter.next();
                pp.print(",");
                pp.nextIndentOff();
                element.prettyPrint(pp);
                pp.println("");
            }
            pp.println("");
            pp.print("]");
            pp.outdent(2);

        } else {
            pp.print("[]");
        }
        printAnnotations(pp);
    }

    @Override public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('[');
        if(!isEmpty()) {
            if(maxDepth == 0) {
                output.append("...");
            } else {
                Iterator<IStrategoTerm> iter = iterator();
                iter.next().writeAsString(output, maxDepth - 1);
                while(iter.hasNext()) {
                    IStrategoTerm element = iter.next();
                    output.append(',');
                    element.writeAsString(output, maxDepth - 1);
                }
            }
        }
        output.append(']');
        appendAnnotations(output, maxDepth);
    }

    @Deprecated @Override public IStrategoTerm get(int index) {
        return getSubterm(index);
    }

    @Deprecated @Override public int size() {
        return getSubtermCount();
    }

    @Deprecated @Override public IStrategoList prepend(IStrategoTerm prefix) {
        return new StrategoList(prefix, this, null);
    }

    @Override public IStrategoTerm head() {
        try {
            return getSubterm(0);
        } catch(IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    @Override public IStrategoList tail() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        return new StrategoArrayList(terms, null, offset + 1);
    }

    @Override public boolean isEmpty() {
        return subtermCount == 0;
    }

    @Override protected boolean doSlowMatch(IStrategoTerm second) {
        if(this == second) {
            return true;
        }
        if(!TermUtils.isList(second)) {
            return false;
        }
        if(this.getSubtermCount() != second.getSubtermCount()) {
            return false;
        }

        if(second instanceof StrategoArrayList) {
            StrategoArrayList other = (StrategoArrayList) second;

            if(!this.getAnnotations().match(other.getAnnotations())) {
                return false;
            }

            //noinspection ArrayEquality
            if(this.terms == other.terms) {
                return this.offset == other.offset && this.subtermCount == other.subtermCount;
            }

            Iterator<IStrategoTerm> termsThis = this.iterator();
            Iterator<IStrategoTerm> termsOther = other.iterator();

            if(!this.isEmpty()) {
                for(IStrategoTerm thisNext = termsThis.next(), otherNext = termsOther.next()
                   ; termsThis.hasNext()
                   ; thisNext = termsThis.next(), otherNext = termsOther.next()) {
                    if(thisNext != otherNext && !thisNext.match(otherNext)) {
                        return false;
                    }
                }
            }

            return true;
        }

        final IStrategoList snd = (IStrategoList) second;

        if(!isEmpty()) {
            IStrategoTerm head = head();
            IStrategoTerm head2 = snd.head();
            if(head != head2 && !head.match(head2))
                return false;

            IStrategoList tail = tail();
            IStrategoList tail2 = snd.tail();

            for(IStrategoList cons = tail, cons2 = tail2; !cons.isEmpty(); cons = cons.tail(), cons2 = cons2.tail()) {
                IStrategoTerm consHead = cons.head();
                IStrategoTerm cons2Head = cons2.head();
                if(!cons.getAnnotations().match(cons2.getAnnotations())) {
                    return false;
                }
                if(consHead != cons2Head && !consHead.match(cons2Head))
                    return false;
            }
        }

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else
            return annotations.match(secondAnnotations);
    }

    /**
     * N.B. this implementation may look strange but it's designed to return the same hashcode as {@link StrategoList#hashFunction()}
     */
    @Override protected int hashFunction() {
        if(isEmpty())
            return 1;

        int i = offset;
        int result = 31 * terms[i].hashCode();
        final int endOffset = offset + subtermCount;
        for(i++; i < endOffset; i++) {
            result = 31 * result + terms[i].hashCode();
        }

        return result;
    }

    @Override public Iterator<IStrategoTerm> iterator() {
        return new StrategoArrayListIterator(this);
    }

    /**
     * Builds an IStrategoList by building up an internal array like ArrayList (doubling the size when we run out of
     * space) and then sharing that array
     */
    public static class ArrayListBuilder implements IStrategoList.Builder {
        private IStrategoTerm[] array;
        private int index = 0;
        private boolean built = false;

        /**
         * Create an array-backed IStrategoList builder. Initializes the array with the given size.
         * @param size initial size of the backing array
         */
        public ArrayListBuilder(int size) {
            size = Math.min(2, size);
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
                array = Arrays.copyOf(array, array.length * 2);
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
}
