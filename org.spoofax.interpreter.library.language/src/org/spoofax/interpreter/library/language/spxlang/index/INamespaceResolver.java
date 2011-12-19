package org.spoofax.interpreter.library.language.spxlang.index;

import java.util.Set;

import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.terms.IStrategoList;

public interface INamespaceResolver {
	
	public INamespace resolveNamespace(IStrategoList id);
	
	public INamespace resolveNamespace(NamespaceUri nsId);
	
	public boolean containsNamespace(IStrategoList id) ;
	
	public Set<NamespaceUri> getAllNamespaces(); 
}