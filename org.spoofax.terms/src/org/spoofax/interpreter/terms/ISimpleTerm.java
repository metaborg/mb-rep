package org.spoofax.interpreter.terms;


/**
 * A simple tree-like interface for terms.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface ISimpleTerm {
	
	int getSubtermCount();
	
	ISimpleTerm getSubterm(int i);

	public<T extends ITermAttachment> T getAttachment(Class<T> type);
	
	boolean isList();
}