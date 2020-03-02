package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
    private final int size;
    private IStrategoTerm head;
    private IStrategoList tail;

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

    @Override
    public IStrategoTerm head() {
        if(head == null)
            throw new NoSuchElementException();
        return head;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public IStrategoList tail() {
        if(tail == null)
            throw new IllegalStateException();
        return tail;
    }

    @Deprecated
    @Override
    public IStrategoList prepend(IStrategoTerm prefix) {
        return new StrategoList(prefix, this, null);
    }

    @Override
    public final IStrategoTerm get(int index) {
        return getSubterm(index);
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        int size = size();
        IStrategoTerm[] clone = new IStrategoTerm[size];
        IStrategoList list = this;
        for(int i = 0; i < size; i++) {
            clone[i] = list.head();
            list = list.tail();
        }
        return clone;
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return TermList.fromIterable(this);
    }


    @Override
    public final int size() {
        return size;
    }

    @Override
    public IStrategoTerm getSubterm(int index) {
        IStrategoList list = this;
        if(index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        for(int i = 0; i < index; i++) {
            if(list.isEmpty())
                throw new IndexOutOfBoundsException("Index out of bounds: " + index);
            list = list.tail();
        }
        return list.head();
    }

    @Override
    public int getSubtermCount() {
        return size;
    }

    @Override
    public int getTermType() {
        return IStrategoTerm.LIST;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(second.getTermType() != IStrategoTerm.LIST)
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

    @Override
    public Iterator<IStrategoTerm> iterator() {
        return new StrategoListIterator(this);
    }

    private Object readResolve() throws ObjectStreamException {
        return this;
    }
}
