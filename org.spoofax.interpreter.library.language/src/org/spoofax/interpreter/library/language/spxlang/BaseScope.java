package org.spoofax.interpreter.library.language.spxlang;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * BaseScope  is an abstract base class that implements Scope Interface
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 27, 2011
 */
abstract class BaseScope implements INamespace {
	
	private static final long serialVersionUID = 2052337390283813190L;
	
	protected final IStrategoConstructor type;
	protected final String  src;
	
	private  final NamespaceId _currentNamespace; 
	private final NamespaceId _enclosingNamespace;
	
	protected final MultiValuePersistentTable _symbols;
	
	public BaseScope(NamespaceId currentNS , IStrategoConstructor type, NamespaceId enclosingNS, ISpxPersistenceManager manager) {
		assert currentNS!= null : "Current NS Identifier is null";
		assert type!= null      : "Non-null Type is expected";
		
		_currentNamespace = currentNS;
		_enclosingNamespace = enclosingNS; 
		
		this.type = type;
		
		src = type().toString() + _enclosingNamespace.UniqueID(); 
			
		_symbols = new MultiValuePersistentTable( manager.getProjectName()+src , manager);
	}

	public void define(SpxSymbol sym,ILogger logger){
		sym.setNamespace(_currentNamespace);
		
		logger.logMessage(src, "Defining Symbol "+ sym);
		
		_symbols.define(sym);
	}
		
	/* 
	 * Returns the enclosing scope of the current scope.
	 * */
	public NamespaceId getEnclosingNamespace() { return _enclosingNamespace; }
	
	public NamespaceId getCurrentNamespace(){ return _currentNamespace; }
	
	public SpxSymbol resolve(IStrategoTerm id, IStrategoTerm type, INamespaceResolver nsResolver, ISpxPersistenceManager manager){
		List<SpxSymbol> lookupResult = getMembers().get(id);
		if( lookupResult!=null){
			List<SpxSymbol> expectedTypedSymbol = SpxSymbol.filterByType(type, lookupResult);
			if(expectedTypedSymbol.size() >0 )
				return lookupResult.get(0) ;
		}

		// Symbols could not be found in the current scope
		// Hence, searching any enclosing scope if it is not 
		// null. After searching global scope, it is not searching
		// anymore.
		if( getEnclosingNamespace() != null) {
			INamespace namespace = getEnclosingNamespace().resolve(nsResolver);
			return namespace.resolve(id, type, nsResolver, manager);
		}	 
		
		return null; // symbol is not found
	}
	
	public Iterable<SpxSymbol> resolveAll(IStrategoTerm id,
			INamespaceResolver nsResolver, ISpxPersistenceManager manager) {
		
		Set<SpxSymbol> retResult = new HashSet<SpxSymbol>();
		
		List<SpxSymbol> lookupResult = getMembers().get(id);
		retResult.addAll(lookupResult);
		
		if( getEnclosingNamespace() != null)
		{
			INamespace namespace = getEnclosingNamespace().resolve(nsResolver);
			Set<SpxSymbol> parentResults  = (Set<SpxSymbol>)namespace.resolveAll(id, nsResolver, manager);
			
			retResult.addAll(parentResults);
		}	 
		
		return retResult;
	}
	
	public IStrategoConstructor type() {
		return type;
	}

	public MultiValuePersistentTable getMembers(){return _symbols;}
}