package org.spoofax.terms.attachments;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class AbstractTermAttachment implements ITermAttachment {
	
	private static final long serialVersionUID = -8243986540022517890L;

	private ITermAttachment next;

	public final ITermAttachment getNext() {
		return next;
	}
	
	public final void setNext(ITermAttachment next) {
		this.next = next;
	}

	@Override
	public ITermAttachment clone() throws CloneNotSupportedException {
		AbstractTermAttachment result = (AbstractTermAttachment) super.clone();
		result.next = null;
		return result;
	}
}
