package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;
import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

interface INamespace extends Serializable {
    
	NamespaceId getEnclosingNamespace();

	NamespaceId getCurrentNamespace();
	
	void define(SpxSymbol sym , ILogger logger) ;
    
	SpxSymbol resolve(IStrategoTerm id, INamespaceResolver nsResolver,ISpxPersistenceManager manager );
    
	Iterable<SpxSymbol> resolveAll(IStrategoTerm id, INamespaceResolver nsResolver,ISpxPersistenceManager manager);
	
	IStrategoConstructor type();
	
	MultiValuePersistentTable getMembers();
}

interface INamespaceResolver {
	
	public INamespace resolveNamespace(NamespaceId nsId);
}

