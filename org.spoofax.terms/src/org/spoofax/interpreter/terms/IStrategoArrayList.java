package org.spoofax.interpreter.terms;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import org.spoofax.terms.StrategoArrayList;
import org.spoofax.terms.StrategoArrayListBuilder;
import org.spoofax.terms.StrategoArrayListIterator;
import org.spoofax.terms.StrategoArrayListTail;
import org.spoofax.terms.StrategoList;
import org.spoofax.terms.TermList;
import org.spoofax.terms.util.TermUtils;

import static org.spoofax.terms.AbstractTermFactory.EMPTY_TERM_ARRAY;

/**
 * Most of the shared implementation for {@link StrategoArrayList} and {@link StrategoArrayListTail}
 */
public interface IStrategoArrayList extends IStrategoTerm, IStrategoList, RandomAccess, InternalIStrategoArrayList {
    static IStrategoArrayList fromCollection(Collection<? extends IStrategoTerm> terms) {
        return new StrategoArrayList(terms.toArray(EMPTY_TERM_ARRAY));
    }

    static StrategoArrayListBuilder arrayListBuilder() {
        return new StrategoArrayListBuilder(16);
    }

    static StrategoArrayListBuilder arrayListBuilder(int size) {
        return new StrategoArrayListBuilder(size);
    }

    @Override
    default boolean internalDoSlowMatch(IStrategoTerm second) {
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
            IStrategoArrayList other = (IStrategoArrayList) second;

            if(!this.getAnnotations().match(other.getAnnotations())) {
                return false;
            }

            //noinspection ArrayEquality
            if(this.internalGetBackingArray() == other.internalGetBackingArray()) {
                return internalGetOffset() == other.internalGetOffset() && this.internalGetEndOffset() == other.internalGetEndOffset();
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
    @Override
    default int internalHashFunction() {
        if(isEmpty())
            return 1;

        int i = internalGetOffset();
        int result = 31 * internalGetBackingArray()[i].hashCode();
        for(i++; i < internalGetEndOffset(); i++) {
            result = 31 * result + internalGetBackingArray()[i].hashCode();
        }

        return result;
    }

    @Override
    default int getSubtermCount() {
        return internalGetEndOffset() - internalGetOffset();
    }

    @Override
    default IStrategoTerm getSubterm(int index) {
        if(index < getSubtermCount()) {
            return internalGetBackingArray()[internalGetOffset() + index];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    default IStrategoTerm[] getAllSubterms() {
        return Arrays.copyOfRange(internalGetBackingArray(), internalGetOffset(), internalGetEndOffset());
    }

    @Override
    default List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(getAllSubterms());
    }

    @Override
    default int getTermType() {
        return IStrategoTerm.LIST;
    }

    @Deprecated @Override
    default void prettyPrint(ITermPrinter pp) {
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
        internalPrintAnnotations(pp);
    }

    @Override
    default void writeAsString(Appendable output, int maxDepth) throws IOException {
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
        internalAppendAnnotations(output, maxDepth);
    }

    @Deprecated @Override
    default IStrategoTerm get(int index) {
        return getSubterm(index);
    }

    @Deprecated @Override
    default int size() {
        return getSubtermCount();
    }

    @Deprecated @Override
    default IStrategoList prepend(IStrategoTerm prefix) {
        return new StrategoList(prefix, this, null);
    }

    @Override
    default IStrategoTerm head() {
        try {
            return getSubterm(0);
        } catch(IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    @Override
    default boolean isEmpty() {
        return getSubtermCount() == 0;
    }

    @Override
    default Iterator<IStrategoTerm> iterator() {
        return new StrategoArrayListIterator(this);
    }

    @Override
    default boolean isList() {
        return true;
    }
}
