package org.spoofax.interpreter.terms;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface ITermAttachment {
	
	/**
	 * The type of this attachment.
	 * Should be implemented non-reflectively, i.e.
	 * 'return ImploderAttachment.class', not 'getClass()'.
	 */
	Class<? extends ITermAttachment> getAttachmentType();
	
	void setNext(ITermAttachment attachment);

	ITermAttachment getNext();
}
