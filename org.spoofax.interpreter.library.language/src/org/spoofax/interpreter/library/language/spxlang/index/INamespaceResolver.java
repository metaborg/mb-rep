package org.spoofax.interpreter.library.language.spxlang.index;

import java.util.Set;

import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;

public interface INamespaceResolver {
	
	public INamespace resolveNamespace(String id);
	
	public INamespace resolveNamespace(NamespaceUri nsId);
	
	public Set<String> getAllNamespaceSpxId(); 
}