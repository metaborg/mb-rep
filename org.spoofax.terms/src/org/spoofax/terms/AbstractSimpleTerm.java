package org.spoofax.terms;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

import javax.annotation.Nullable;


/**
 * Base class for simple term implementations.
 */
public abstract class AbstractSimpleTerm implements ISimpleTerm, Cloneable {

    private static final long serialVersionUID = 1L;

    private ITermAttachment attachment;

    @SuppressWarnings("unchecked")
    @Override
    @Nullable public <T extends ITermAttachment> T getAttachment(@Nullable TermAttachmentType<T> type) {
        if(type == null)
            return (T) this.attachment;
        for(ITermAttachment a = this.attachment; a != null; a = a.getNext()) {
            if(a.getAttachmentType() == type)
                return (T) a;
        }
        return null;
    }

    @Override
    public void putAttachment(ITermAttachment attachment) {
        if(attachment == null)
            return;
        if(this.attachment == null) {
            this.attachment = attachment;
        } else {
            TermAttachmentType<?> newType = attachment.getAttachmentType();
            if(this.attachment.getAttachmentType() == newType) {
                attachment.setNext(this.attachment.getNext());
                this.attachment = attachment;
            } else {
                ITermAttachment previous = this.attachment;
                for(ITermAttachment a = previous.getNext(); a != null; a = a.getNext()) {
                    if(a.getAttachmentType() == newType) {
                        attachment.setNext(a.getNext());
                        previous.setNext(attachment);
                        break;
                    }
                    previous = a;
                }
                previous.setNext(attachment);
            }
        }
    }

    @Override
    @Nullable public ITermAttachment removeAttachment(TermAttachmentType<?> type) {
        if(attachment != null) {
            if(attachment.getAttachmentType() == type) {
                ITermAttachment old = attachment;
                attachment = attachment.getNext();
                old.setNext(null);
                return old;
            } else {
                ITermAttachment previous = this.attachment;
                for(ITermAttachment a = attachment.getNext(); a != null; a = a.getNext()) {
                    if(a.getAttachmentType() == type) {
                        previous.setNext(a.getNext());
                        a.setNext(null);
                        return a;
                    }
                    previous = a;
                }
            }
        }
        return null;
    }

    protected final ITermAttachment attachment() {
        return attachment;
    }

    protected void clearAttachments() {
        attachment = null;
    }

}
