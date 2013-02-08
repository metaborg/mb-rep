package org.spoofax.interpreter.library.language.spxlang.index;


public interface INamespaceFactory {
	
	public Iterable<INamespace> newNamespaces(SpxSemanticIndexFacade idxFacade); 
}