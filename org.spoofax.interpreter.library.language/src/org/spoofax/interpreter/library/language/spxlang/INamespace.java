package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public interface INamespace extends Serializable {
    
	INamespace getEnclosingNamespace(INamespaceResolver resolver);

	INamespace getCurrentNamespace(INamespaceResolver resolver);
	
	void define(SpxSymbolTableEntry entry, ILogger logger) ;
    
	SpxSymbol resolve(IStrategoTerm id, IStrategoTerm type, INamespace searchedBy , SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException;
    
	Iterable<SpxSymbol> resolveAll(IStrategoTerm searchingFor, IStrategoTerm type, INamespace searchedBy , SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException;
	
	Iterable<SpxSymbol> resolveAll(IStrategoTerm searchingFor, IStrategoTerm type, SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException;
	
	Iterable<SpxSymbol> resolveAll(IStrategoTerm searchingFor, INamespace searchedBy ,  SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException;
	
	IStrategoConstructor type();
	
	MultiValuePersistentTable getMembers();
	
	NamespaceUri namespaceUri();
	
	boolean isInternalNamespace() ;
}



