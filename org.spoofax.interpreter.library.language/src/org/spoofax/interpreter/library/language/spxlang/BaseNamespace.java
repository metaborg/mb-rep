package org.spoofax.interpreter.library.language.spxlang;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jdbm.PrimaryMap;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * BaseScope  is an abstract base class that implements Scope Interface
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 27, 2011
 */
public abstract class BaseNamespace implements INamespace {
	
	private static final long serialVersionUID = 2052337390283813190L;
	
	protected final IStrategoConstructor type;
	protected final String  src;

	private  final NamespaceUri _currentNamespaceId; 
	private final NamespaceUri _enclosingNamespaceId;
	
	protected final MultiValuePersistentTable _symbols;

	public NamespaceUri namespaceUri() {return _currentNamespaceId;}
	
	protected NamespaceUri enclosingNamespaceUri() { return _enclosingNamespaceId ; } 
	
	public BaseNamespace(NamespaceUri currentNamespace , IStrategoConstructor type, ISpxPersistenceManager manager, NamespaceUri enclosingNamespace) {
		assert currentNamespace!= null : "Current Namespace Identifier is null";
		assert type!= null      : "Non-null Type is expected";
		
		_currentNamespaceId = currentNamespace;
		_enclosingNamespaceId = enclosingNamespace; 
		
		this.type = type;
		
		src = (_enclosingNamespaceId!= null) ? type.getName() + _currentNamespaceId.id() :  type.getName()   ; 
			
		_symbols = new MultiValuePersistentTable();
	}

	public void define(SpxSymbolTableEntry entry, ILogger logger){
		
		entry.value.setNamespace(_currentNamespaceId);
		
		logger.logMessage(src, "define | Defining Symbol "+ entry.value + " in "+ _currentNamespaceId);
		
		_symbols.define(entry);
	}

	/* 
	 * Returns the enclosing scope of the current scope.
	 * */
	public INamespace getEnclosingNamespace(INamespaceResolver rs) throws SpxSymbolTableException { return (_enclosingNamespaceId != null) ? _enclosingNamespaceId.resolve(rs) : null; }
	
	public INamespace getCurrentNamespace(INamespaceResolver rs) throws SpxSymbolTableException{ return _currentNamespaceId.resolve(rs); }
	
	public SpxSymbol resolve(IStrategoTerm id, IStrategoTerm type, INamespace searchedBy, SpxSemanticIndexFacade  facade) throws SpxSymbolTableException{
		facade.persistenceManager().logMessage(this.src, "resolve | Resolving Symbol in " + this.namespaceUri().id() +  " . Key :  " + id + " origin Namespace: " + searchedBy.namespaceUri().id() );
		
		assert type instanceof IStrategoConstructor : "Type is expected to be IStrategoConstructor" ;
			
		List<SpxSymbol> lookupResult = getMembers().resolve(id);
		if( lookupResult!=null){
			List<SpxSymbol> expectedTypedSymbol = SpxSymbol.filterByType((IStrategoConstructor)type, lookupResult);
		
			if(expectedTypedSymbol.size() >0 )
				return lookupResult.get(0) ;
		}
	
		// Symbols could not be found in the current scope
		// Hence, searching any enclosing(parent) scope if it is not 
		// null. After searching global scope, it is not searching
		// anymore.
		INamespace namespace = getEnclosingNamespace(facade.persistenceManager().spxSymbolTable());
		if( namespace  != null) {
			//checks whether searching to the enclosing scope is allowed.
			if( shouldSearchInEnclosingNamespace( searchedBy))
				return namespace.resolve(id, type, this, facade);
		}	 
		
		return null; // symbol is not found
	}
	
	public Iterable<SpxSymbol> resolveAll(IStrategoTerm id, INamespace searchedBy, SpxSemanticIndexFacade  facade) throws SpxSymbolTableException {
		
		facade.persistenceManager().logMessage(this.src, "resolveAll(Base) | Resolving Symbol in " + this.namespaceUri().id() +  " . Key :  " + id + " origin Namespace: " + searchedBy.namespaceUri().id() );
		
		Set<SpxSymbol> retResult = new HashSet<SpxSymbol>();
		
		List<SpxSymbol> lookupResult = getMembers().resolve(id);
		retResult.addAll(lookupResult);
		
		INamespace namespace = getEnclosingNamespace(facade.persistenceManager().spxSymbolTable());
		//checking whether resolved namespace is Null. In that case, all the scopes are covered.
		//also checking that the resolved namespace is not equal to the current namespace 
		//that we already have searched - to avoid any cycle in the hierarchy.
		if( namespace  != null && !namespace.equals(this)){
			//checks whether searching to the enclosing scope is allowed.
			if( shouldSearchInEnclosingNamespace(searchedBy)){	
				Set<SpxSymbol> parentResults  = (Set<SpxSymbol>)namespace.resolveAll(id, this ,facade);
				retResult.addAll(parentResults);
			}
		}	 
		return retResult;
	}
	
	public Iterable<SpxSymbol> resolveAll(IStrategoTerm id, IStrategoTerm type, INamespace searchedBy,SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException{
		
		return SpxSymbol.filterByType((IStrategoConstructor) type, this.resolveAll(id, searchedBy, spxFacade)) ;
	}
	
	
	/* Resolving symbol for given type and symbol id .
	 * 
	 * (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.INamespace#resolveAll(org.spoofax.interpreter.terms.IStrategoTerm, org.spoofax.interpreter.terms.IStrategoTerm, org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	public Iterable<SpxSymbol> resolveAll(IStrategoTerm searchingFor, IStrategoTerm type, SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException{
		return resolveAll(searchingFor, type,  this, spxFacade);
	}
	
	
	public IStrategoConstructor type() {
		return type;
	}

	public MultiValuePersistentTable getMembers(){return _symbols;}

	public boolean isInternalNamespace() { return false;  }

	protected boolean shouldSearchInInternalNamespace( INamespace searchedBy) {
		// If searchedBy Namespace is enclosingNamespace of CurrentNamespace 
		// Search for internal symbol scopes as well
		return searchedBy.namespaceUri().equals(this.enclosingNamespaceUri()) || searchedBy.namespaceUri().equals(this.namespaceUri());  
	}
	
	/**
	 * Base Condition of the lookup : 
	 * Allow search enclosing Namesapce only if searchedBy is not enclosing Namespace. 
	 *  
	 * @param searchedBy
	 * @return True if enclosing Namespace != searachedBy  ; otherwise false.
	 */
	protected boolean shouldSearchInEnclosingNamespace(INamespace searchedBy) {
		// search enclosing Namesapce only if searchedBy is not enclosing Namespace
		return !(searchedBy.namespaceUri().equals(this.enclosingNamespaceUri())); 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BaseNamespace [type=" + type + ", _currentNamespaceId=" + _currentNamespaceId.id() + "]";
	}
}