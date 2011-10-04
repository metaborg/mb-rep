package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoList;

public interface INamespaceResolver {
	
	public INamespace resolveNamespace(IStrategoList id);
	
	public INamespace resolveNamespace(NamespaceUri nsId);
	
	public boolean containsNamespace(IStrategoList id) ;
}