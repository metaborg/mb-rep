package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoArrayList;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.attachments.ITermAttachment;

import java.io.IOException;

/**
 * @see IStrategoArrayList for most of the shared implementation for this class and {@link StrategoArrayListTail}
 */
public class StrategoArrayList extends StrategoTerm implements IStrategoArrayList {
    private static final long serialVersionUID = -1746012089187246512L;

    final IStrategoTerm[] backingArray;
    private final int offset;
    final int endOffset;
    ITermAttachment[] tailAttachments;

    public StrategoArrayList(IStrategoTerm... backingArray) {
        this(backingArray, null);
    }

    public StrategoArrayList(IStrategoTerm[] backingArray, IStrategoList annotations) {
        this(backingArray, annotations, 0, backingArray.length);
    }

    protected StrategoArrayList(IStrategoTerm[] backingArray, IStrategoList annotations, int offset, int endOffset) {
        super(annotations);
        if(offset > backingArray.length) {
            throw new IllegalArgumentException(
                "Offset (" + offset + ") is larger than backing array (" + backingArray.length + ")");
        }
        if(endOffset > backingArray.length) {
            throw new IllegalArgumentException(
                "Length (" + endOffset + ") is larger than backing array (" + backingArray.length + ")");
        }
        this.backingArray = backingArray;
        this.offset = offset;
        this.endOffset = endOffset;
        this.tailAttachments = null;
    }

    @Override
    public IStrategoTerm[] internalGetBackingArray() {
        return backingArray;
    }

    @Override
    public int internalGetOffset() {
        return offset;
    }

    @Override
    public int internalGetEndOffset() {
        return endOffset;
    }

    ITermAttachment[] internalGetTailAttachments() {
        if(tailAttachments == null) {
            tailAttachments = new ITermAttachment[getSubtermCount()];
        }
        return tailAttachments;
    }

    @Override
    public void internalAppendAnnotations(Appendable output, int maxDepth) throws IOException {
        appendAnnotations(output, maxDepth);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void internalPrintAnnotations(ITermPrinter pp) {
        printAnnotations(pp);
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        return internalDoSlowMatch(second);
    }

    @Override
    protected int hashFunction() {
        return internalHashFunction();
    }

    @Override
    public IStrategoList tail() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        return new StrategoArrayListTail(this, offset + 1);
    }
}
