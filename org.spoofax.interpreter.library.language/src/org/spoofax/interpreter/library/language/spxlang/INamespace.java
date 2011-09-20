package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;
import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

interface INamespace extends Serializable {
    
	public NamespaceId getEnclosingNamespace();

	public NamespaceId getCurrentNamespace();
	
	public void define(SpxSymbol sym , ILogger logger) ;
    
	public Iterable<SpxSymbol> resolve(INamespaceResolver nsResolver, IStrategoTerm id, SearchPattern pattern , ILogger logger);
    
	IStrategoConstructor type(); 
}

enum SearchPattern
{
	ALL,
	ONE
}


interface INamespaceResolver {
	
	public INamespace resolveScope(NamespaceId nsId);
}

