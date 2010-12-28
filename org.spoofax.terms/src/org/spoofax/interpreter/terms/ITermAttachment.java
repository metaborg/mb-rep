package org.spoofax.interpreter.terms;

public interface ITermAttachment {
	<T extends ITermAttachment> T tryGetAttachment(Class<T> type);
	
	void addAttachment(ITermAttachment attachment);
}
