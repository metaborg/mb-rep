package org.spoofax.terms;

import java.io.IOException;
import org.spoofax.interpreter.terms.IStrategoArrayList;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.attachments.ITermAttachment;

public class StrategoArrayListTail implements IStrategoArrayList {
    private final StrategoArrayList original;
    private final int offset;

    public StrategoArrayListTail(StrategoArrayList original, int offset) {
        this.original = original;
        this.offset = offset;
    }

    @Override
    public IStrategoTerm[] internalGetBackingArray() {
        return original.internalGetBackingArray();
    }

    @Override
    public int internalGetOffset() {
        return offset;
    }

    @Override
    public int internalGetEndOffset() {
        return original.internalGetEndOffset();
    }

    @Override
    public void internalAppendAnnotations(Appendable output, int maxDepth) {
        // do nothing, these tails don't have annotations
    }

    @Override
    public void internalPrintAnnotations(ITermPrinter pp) {
        // do nothing, these tails don't have annotations
    }

    @Override
    public IStrategoList getAnnotations() {
        return new StrategoList(null);
    }

    // This is a copy of StrategoTerm#match
    @Override
    public boolean match(IStrategoTerm second) {
        if(this == second)
            return true;
        if(second == null)
            return false;

        return hashCode() == second.hashCode() && internalDoSlowMatch(second);
    }

    // This is a copy of StrategoTerm#toString
    @Override
    public String toString(int maxDepth) {
        StringBuilder result = new StringBuilder();
        try {
            writeAsString(result, maxDepth);
        } catch(IOException e) {
            throw new RuntimeException(e); // shan't happen
        }
        return result.toString();
    }

    @Override
    public ITermAttachment internalGetAttachment() {
        return original.internalGetTailAttachments()[offset - original.internalGetOffset()];
    }

    @Override
    public void internalSetAttachment(ITermAttachment attachment) {
        original.internalGetTailAttachments()[offset - original.internalGetOffset()] = attachment;
    }

    @Override
    public IStrategoList tail() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        return new StrategoArrayListTail(original, offset + 1);
    }
}
