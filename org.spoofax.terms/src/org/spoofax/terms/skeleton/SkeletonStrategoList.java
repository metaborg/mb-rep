/*
 * Copyright (c) 2011, Karl Trygve Kalleberg <karltk near strategoxt dot org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms.skeleton;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.StrategoListIterator;
import org.spoofax.terms.StrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;
import org.spoofax.terms.util.NotImplementedException;

import java.io.IOException;
import java.util.Iterator;

public abstract class SkeletonStrategoList extends StrategoTerm implements IStrategoList, Iterable<IStrategoTerm> {

    private static final long serialVersionUID = 624120573663698628L;

    private IStrategoTerm head;

    private IStrategoList tail;

    /**
     * Creates a new list.
     *
     * @see #prepend(IStrategoTerm) Adds a new head element to a list.
     */
    protected SkeletonStrategoList(IStrategoList annotations) {
        super(annotations);
    }

    @Deprecated
    public final IStrategoList prepend(IStrategoTerm prefix) {
        throw new NotImplementedException();
    }

    @Deprecated
    public final IStrategoTerm get(int index) {
        throw new NotImplementedException();
    }

    public final int size() {
        return getSubtermCount();
    }

    public final int getTermType() {
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

    public final void prettyPrint(ITermPrinter pp) {
        throw new NotImplementedException();
    }

    public final void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('[');
        if(!isEmpty()) {
            if(maxDepth == 0) {
                output.append("...");
            } else {
                IStrategoTerm[] kids = getAllSubterms();
                kids[0].writeAsString(output, maxDepth - 1);
                for(int i = 1; i < kids.length; i++) {
                    output.append(',');
                    kids[i].writeAsString(output, maxDepth - 1);
                }
            }
        }
        output.append(']');
        appendAnnotations(output, maxDepth);
    }

    @Override
    public int hashFunction() {
        /* UNDONE: BasicStrategoTerm hash; should use cons/nil hash instead
        long hc = 4787;
        for (IStrategoList cur = this; !cur.isEmpty(); cur = cur.tail()) {
            hc *= cur.head().hashCode();
        }
        return (int)(hc >> 2);
        */
        final int prime = 71;
        int result = 1;
        result = prime * result + ((head == null) ? 0 : head.hashCode());
        result = prime * result + ((tail == null) ? 0 : tail.hashCode());
        return result;
    }

    public final Iterator<IStrategoTerm> iterator() {
        return new StrategoListIterator(this);
    }

    @Override
    public final String toString(int maxDepth) {
        return super.toString(maxDepth);
    }

    @Override
    public final <T extends ITermAttachment> T getAttachment(TermAttachmentType<T> type) {
        return super.getAttachment(type);
    }

    @Override
    public final void putAttachment(ITermAttachment attachment) {
        super.putAttachment(attachment);
    }

    @Override
    public final ITermAttachment removeAttachment(TermAttachmentType<?> type) {
        return super.removeAttachment(type);
    }

    @Override
    protected final void clearAttachments() {
        super.clearAttachments();
    }
}
