package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;
import org.spoofax.terms.util.TermUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import javax.annotation.Nullable;

import static org.spoofax.terms.AbstractTermFactory.EMPTY_TERM_ARRAY;

public class StrategoArrayList extends StrategoTerm implements IStrategoList, RandomAccess {
    private static final long serialVersionUID = -1746012089187246512L;

    final IStrategoTerm[] terms;
    private final int offset;
    final int endOffset;
    final ITermAttachment[] tailAttachments;

    public StrategoArrayList(IStrategoTerm... terms) {
        this(terms, null);
    }

    public StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations) {
        this(terms, annotations, 0, terms.length, new ITermAttachment[terms.length]);
    }

    protected StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations, int offset, int endOffset) {
        this(terms, annotations, offset, endOffset, new ITermAttachment[terms.length]);
    }

    protected StrategoArrayList(IStrategoTerm[] terms, IStrategoList annotations, int offset, int endOffset, ITermAttachment[] tailAttachments) {
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
        this.endOffset = endOffset;
        this.tailAttachments = tailAttachments;
        if(offset > 0) {
            final ITermAttachment attachment = tailAttachments[offset-1];
            if(attachment != null) {
                super.putAttachment(attachment);
            }
        }
    }

    public static StrategoArrayList fromCollection(Collection<? extends IStrategoTerm> terms) {
        return new StrategoArrayList(terms.toArray(EMPTY_TERM_ARRAY));
    }

    public static StrategoArrayListBuilder arrayListBuilder() {
        return new StrategoArrayListBuilder(16);
    }

    public static StrategoArrayListBuilder arrayListBuilder(int size) {
        return new StrategoArrayListBuilder(size);
    }

    @Override public int getSubtermCount() {
        return endOffset - offset;
    }

    @Override public IStrategoTerm getSubterm(int index) {
        if(index < getSubtermCount()) {
            return terms[offset + index];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return Arrays.copyOfRange(terms, offset, endOffset);
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
        return new StrategoArrayList(terms, null, offset + 1, endOffset, tailAttachments);
    }

    @Override public boolean isEmpty() {
        return getSubtermCount() == 0;
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
                return offset == other.offset && this.endOffset == other.endOffset;
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
        for(i++; i < endOffset; i++) {
            result = 31 * result + terms[i].hashCode();
        }

        return result;
    }

    @Override public Iterator<IStrategoTerm> iterator() {
        return new StrategoArrayListIterator(this);
    }

    @Override
    public void putAttachment(ITermAttachment attachment) {
        setTailAttachment(attachment);
        super.putAttachment(attachment);
    }

    @Override
    @Nullable
    public ITermAttachment removeAttachment(TermAttachmentType<?> type) {
        final ITermAttachment attachment = super.removeAttachment(type);
        setTailAttachment(attachment());
        return attachment;
    }

    @Override
    protected void clearAttachments() {
        setTailAttachment(null);
        super.clearAttachments();
    }

    private void setTailAttachment(ITermAttachment attachment) {
        if(offset > 0) {
            tailAttachments[offset -1] = attachment;
        }
    }

}
