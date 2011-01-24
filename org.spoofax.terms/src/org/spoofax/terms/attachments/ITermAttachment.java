package org.spoofax.terms.attachments;

import java.io.Serializable;

/**
 * An attachment for a term.
 * 
 * Can be part of a linked list chain of attachment;
 * should not be shared between multiple terms.
 *  
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface ITermAttachment extends Serializable {
	
	TermAttachmentType<?> getAttachmentType();
	
	void setNext(ITermAttachment attachment);

	ITermAttachment getNext();
}
