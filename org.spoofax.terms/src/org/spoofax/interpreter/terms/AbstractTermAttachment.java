package org.spoofax.interpreter.terms;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class AbstractTermAttachment implements ITermAttachment {
	
	private ITermAttachment next;

	public final ITermAttachment getNext() {
		return next;
	}
	
	public final void setNext(ITermAttachment next) {
		this.next = next;
	}

}
