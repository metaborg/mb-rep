package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.TermUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;

/**
 * A basic stratego list implementation using a linked-list data structure.
 */
public class StrategoList extends StrategoTerm implements IStrategoList {

    /**
     * @see #hashFunction()
     * @see TermFactory#EMPTY_LIST  The singleton maximally shared empty list instance.
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    static final int EMPTY_LIST_HASH = 1 * 71 * 71;
    private static final long serialVersionUID = 624120573663698628L;
    private final IStrategoTerm head;
    private final IStrategoList tail;
    // Do not access this field directly, but access it through getSubterms().
    // This field is null until it is accessed for the first time.
    private @Nullable TermList subterms = null;
    // The size is calculated independently of the elements.
    private final int size;

    /**
     * Creates a new list.
     *
     * @param head the head of the list
     * @param tail the tail of the list
     * @param annotations the annotations on the term
     */
    public StrategoList(IStrategoTerm head, IStrategoList tail, IStrategoList annotations) {
        super(annotations);
        if ((head == null) != (tail == null)) throw new IllegalArgumentException("Both the head and tail must be non-null or both null.");

        this.head = head;
        this.tail = tail;
        this.size = (head == null ? 0 : 1) + (tail == null ? 0 : tail.size());
    }

    /**
     * Creates a new empty list.
     *
     * @param annotations the annotations on the term
     */
    public StrategoList(IStrategoList annotations) {
        this(null, null, annotations);
    }

    private static IStrategoTerm[] getAllSubtermsAsArray(StrategoList list) {
        IStrategoTerm[] clone = new IStrategoTerm[list.size];
        IStrategoList rest = list;
        for(int i = 0; i < list.size; i++) {
            clone[i] = rest.head();
            rest = rest.tail();
        }
        return clone;
    }

    @Override
    public IStrategoTerm head() {
        if(head == null) throw new NoSuchElementException();
        return head;
    }

    @Override
    public boolean isEmpty() {
        return getSubterms().isEmpty();
    }

    @Override
    public IStrategoList tail() {
        if(tail == null) throw new IllegalStateException();
        return tail;
    }

    @Deprecated
    @Override
    public IStrategoList prepend(IStrategoTerm prefix) {
        return new StrategoList(prefix, this, null);
    }

    @Override
    public final IStrategoTerm get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return getSubterms().get(index);
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        if (this.subterms == null) {
            this.subterms = TermList.of(getAllSubtermsAsArray(this));
        }
        return this.subterms;
    }


    @Override
    public final int size() {
        return this.size;
    }

    @Override
    public IStrategoTerm getSubterm(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return getSubterms().get(index);
    }

    @Override
    public int getSubtermCount() {
        return this.size;
    }

    @Override
    public int getTermType() {
        return IStrategoTerm.LIST;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(!TermUtils.isList(second))
            return false;

        final IStrategoList snd = (IStrategoList) second;
        if(size() != snd.size())
            return false;

        if(!isEmpty()) {
            IStrategoTerm head = head();
            IStrategoTerm head2 = snd.head();
            if(head != head2 && !head.match(head2))
                return false;

            IStrategoList tail = tail();
            IStrategoList tail2 = snd.tail();

            // TODO: test equality of annos on cons nodes (see BasicStrategoList)
            for(IStrategoList cons = tail, cons2 = tail2; !cons.isEmpty(); cons = cons.tail(), cons2 = cons2.tail()) {
                IStrategoTerm consHead = cons.head();
                IStrategoTerm cons2Head = cons2.head();
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

    @Deprecated
    @Override
    public void prettyPrint(ITermPrinter pp) {
        if(!isEmpty()) {
            pp.println("[");
            pp.indent(2);
            head().prettyPrint(pp);
            for(IStrategoList cur = tail(); !cur.isEmpty(); cur = cur.tail()) {
                pp.print(",");
                pp.nextIndentOff();
                cur.head().prettyPrint(pp);
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

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('[');
        if(!isEmpty()) {
            if(maxDepth == 0) {
                output.append("...");
            } else {
                head().writeAsString(output, maxDepth - 1);
                for(IStrategoList cur = tail(); !cur.isEmpty(); cur = cur.tail()) {
                    output.append(',');
                    cur.head().writeAsString(output, maxDepth - 1);
                }
            }
        }
        output.append(']');
        appendAnnotations(output, maxDepth);
    }

    @Override
    public int hashFunction() {
        if(head == null)
            return 1;

        final int prime = 31;
        int result = prime * head.hashCode();

        if(tail == null)
            return result;

        IStrategoList tail = this.tail;
        while(!tail.isEmpty()) {
            result = prime * result + tail.head().hashCode();
            tail = tail.tail();
        }

        return result;
    }

    private Object readResolve() throws ObjectStreamException {
        return this;
    }
}
