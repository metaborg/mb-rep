package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbol;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolKey;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableEntry;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface INamespace extends Serializable {
    
	INamespace getEnclosingNamespace(INamespaceResolver resolver) throws SpxSymbolTableException;

	INamespace getCurrentNamespace(INamespaceResolver resolver) throws SpxSymbolTableException;
	
	INamespace define(SpxSymbolTableEntry entry, ILogger logger) ;
    
	SpxSymbol resolve(IStrategoTerm searchingFor, IStrategoTerm type, INamespace searchedBy , SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException;
    
	Iterable<SpxSymbol> resolveAll(IStrategoTerm searchingFor, IStrategoTerm type, SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException;
	
	Iterable<SpxSymbol> resolveAll(IStrategoTerm searchingFor, IStrategoTerm ofType, INamespace searchedByNamepsace , SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException;
	
	Map<SpxSymbolKey, List<SpxSymbol>> getMembers();
	
	NamespaceUri namespaceUri();
	
	boolean isInternalNamespace() ;
	
	public IStrategoAppl toTypedQualifiedName(SpxSemanticIndexFacade facade);

	void clear();	
}



