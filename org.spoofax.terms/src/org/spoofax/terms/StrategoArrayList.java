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

public class StrategoArrayList extends AbstractStrategoList implements RandomAccess {
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

    @Override public int getTermType() {
        return IStrategoTerm.LIST;
    }

    @Deprecated @Override public IStrategoTerm get(int index) {
        return getSubterm(index);
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
        if(!(second instanceof StrategoArrayList)) {
            return super.doSlowMatch(second);
        }
        if(this == second) {
            return true;
        }
        if(this.getSubtermCount() != second.getSubtermCount()) {
            return false;
        }

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

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else
            return annotations.match(secondAnnotations);
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
