package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	INamespace define(SpxSymbolTableEntry entry, SpxSemanticIndexFacade f);
	
	Set<SpxSymbol> undefineSymbols(IStrategoTerm searchingFor, IStrategoTerm type , SpxSemanticIndexFacade  facade) ;
    
	SpxSymbol resolve(IStrategoTerm searchingFor, IStrategoTerm type, INamespace searchedBy , SpxSemanticIndexFacade spxFacade, int lookupDepth) throws SpxSymbolTableException;
    
	Collection<SpxSymbol> resolveAll(SpxSemanticIndexFacade spxFacade, IStrategoTerm searchingFor, IStrategoTerm type, int lookupDepth, boolean retrunDuplicate) throws SpxSymbolTableException;
	
	Collection<SpxSymbol>  resolveAll(SpxSemanticIndexFacade spxFacade, IStrategoTerm searchingFor, IStrategoTerm ofType , INamespace searchedByNamepsace, int lookupDepth, boolean returnDuplicate) throws SpxSymbolTableException;
	
	Map<SpxSymbolKey, List<SpxSymbol>> getMembers();
	
	NamespaceUri namespaceUri();
	
	NamespaceUri enclosingNamespaceUri(); 
	
	boolean isInternalNamespace() ;
	
	String getAbosoluteFilePath();
	
	IStrategoAppl toTypedQualifiedName(SpxSemanticIndexFacade facade);

	void clear();	
}
