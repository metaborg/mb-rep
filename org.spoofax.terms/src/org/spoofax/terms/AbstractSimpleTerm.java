package org.spoofax.terms;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.terms.attachments.ITermAttachment;


/**
 * Base class for simple term implementations.
 */
public abstract class AbstractSimpleTerm implements ISimpleTerm, Cloneable {

    private static final long serialVersionUID = 1L;

    private ITermAttachment attachment;

    protected void clearAttachments() {
        attachment = null;
    }

    @Override
    public ITermAttachment internalGetAttachment() {
        return attachment;
    }

    @Override
    public void internalSetAttachment(ITermAttachment attachment) {
        this.attachment = attachment;
    }

}
