package org.spoofax.interpreter.terms;

public abstract class AbstractTermAttachment implements ITermAttachment {
	
	private ITermAttachment next;
	
	protected abstract boolean isAttachmentType(Class<?> type);

	@SuppressWarnings("unchecked")
	public <T extends ITermAttachment> T tryGetAttachment(Class<T> type) {
		if (isAttachmentType(type)) {
			return (T) this;
		} else if (next != null) {
			return next.tryGetAttachment(type);
		} else {
			return null;
		}
	}

	public void addAttachment(ITermAttachment attachment) {
		if (next != null) {
			next = attachment;
		} else {
			next.addAttachment(attachment);
		}
	}

}
