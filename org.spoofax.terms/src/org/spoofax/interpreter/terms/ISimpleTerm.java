package org.spoofax.interpreter.terms;

import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;


/**
 * A simple tree-like interface for terms.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface ISimpleTerm {
	
	int getSubtermCount();
	
	ISimpleTerm getSubterm(int i);

	/**
	 * Gets the term attachment of the specified type, if one is available for
	 * this term, or returns null.
	 * 
	 * @param attachmentType
	 *            The desired term attachment type, or null if the first
	 *            attachment should be returned
	 */
	<T extends ITermAttachment> T getAttachment(TermAttachmentType<T> type);

	void putAttachment(ITermAttachment resourceAttachment);
    
    void removeAttachment(TermAttachmentType<?> attachmentType);
	
	boolean isList();
}