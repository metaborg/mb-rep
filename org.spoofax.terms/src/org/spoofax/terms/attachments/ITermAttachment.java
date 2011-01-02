package org.spoofax.terms.attachments;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface ITermAttachment {
	
	TermAttachmentType<?> getAttachmentType();
	
	void setNext(ITermAttachment attachment);

	ITermAttachment getNext();
}
