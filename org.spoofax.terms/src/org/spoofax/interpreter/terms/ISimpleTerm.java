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

	public<T extends ITermAttachment> T getAttachment(TermAttachmentType<T> type);

	void putAttachment(ITermAttachment resourceAttachment);
	
	boolean isList();
}