package org.spoofax.interpreter.library.language.spxlang;


public interface INamespaceFactory {
	
	public Iterable<INamespace> newNamespaces(SpxSemanticIndexFacade idxFacade); 
}