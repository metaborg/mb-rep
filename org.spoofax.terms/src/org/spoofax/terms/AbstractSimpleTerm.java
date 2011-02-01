package org.spoofax.terms;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

public abstract class AbstractSimpleTerm implements ISimpleTerm, Cloneable {
    
    private ITermAttachment attachment;
    
    @SuppressWarnings("unchecked")
	public<T extends ITermAttachment> T getAttachment(TermAttachmentType<T> type) {
    	if (type == null)
    		return (T) this.attachment;
    	for (ITermAttachment a = this.attachment; a != null; a = a.getNext()) {
    		if (a.getAttachmentType() == type)
    			return (T) a;
    	}
		return null;
    }
    
    public void putAttachment(ITermAttachment attachment) {
    	if (attachment == null) return;
    	assert !(this instanceof IStrategoTerm)
    		|| ((IStrategoTerm) this).getStorageType() == IStrategoTerm.MUTABLE
    		: "Attachments only supported for mutable, non-shared terms; failed for " + this;
    	assert attachment.getNext() == null;
    	if (this.attachment == null) {
    		this.attachment = attachment;
    	} else {
    		TermAttachmentType<?> newType = attachment.getAttachmentType();
    		if (this.attachment.getAttachmentType() == newType) {
    			attachment.setNext(this.attachment.getNext());
    			this.attachment = attachment;
    		} else {
    			ITermAttachment previous = this.attachment;
    			for (ITermAttachment a = previous.getNext(); a != null; a = a.getNext()) {
	        		if (a.getAttachmentType() == newType) {
	        			attachment.setNext(a.getNext());
	        			previous.setNext(attachment);
	        		}
	        	}
    			previous.setNext(attachment);
    		}
    	}
    }
    
    public void removeAttachment(TermAttachmentType<?> attachmentType) {
    	throw new NotImplementedException();
    }
}
