package org.spoofax.interpreter.library.language.spxlang;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * BaseScope  is an abstract base class that implements Scope Interface
 * 
 * @author Md. Adil Akhter
 */
public abstract class BaseNamespace implements INamespace {
	
	private static final long serialVersionUID = 2052337390283813190L;
	
	protected final String  src;
	
	private final NamespaceUri _currentNamespaceId; 
	private final NamespaceUri _enclosingNamespaceId;
	
	/**
	 * Stores all the symbols from the current namespace. 
	 * @serialField
	 */
	protected HashMap<SpxSymbolKey, List<SpxSymbol>> symbols;

	public NamespaceUri namespaceUri() {return _currentNamespaceId;}
	
	protected NamespaceUri enclosingNamespaceUri() { return _enclosingNamespaceId ; } 
	
	public abstract IStrategoAppl toTypedQualifiedName(SpxSemanticIndexFacade facade);
	
	protected BaseNamespace(NamespaceUri currentNamespace , IStrategoConstructor type, ISpxPersistenceManager manager, NamespaceUri enclosingNamespace) {
		assert currentNamespace!= null : "Current Namespace Identifier is null";
		assert type!= null      : "Non-null Type is expected";
		
		_currentNamespaceId = currentNamespace;
		_enclosingNamespaceId = enclosingNamespace; 
		
		src = (_enclosingNamespaceId!= null) ? type.getName() + _currentNamespaceId.id() :  type.getName()   ; 
			
		symbols = new HashMap<SpxSymbolKey, List<SpxSymbol>>();
	}

	public INamespace define(SpxSymbolTableEntry entry, ILogger logger){
		
		entry.value.setNamespace(_currentNamespaceId);
		
		logger.logMessage(src, "define | Defining Symbol "+ entry.value + " in "+ _currentNamespaceId);
		
		defineSymbol(entry); 
		return this;
	}
	
	/**
	 * Defines symbol in this namespace. Define does not replace  
	 * old symbol mapped using the key with the new one. It just adds the 
	 * new symbol at the end of the multivaluelist. 
	 * 
	 * @param key - The key that the symbol will be mapped to .
	 * @param symbol - The symbol to store. 
	 */
	private void defineSymbol(SpxSymbolTableEntry entry){
		SpxSymbolKey key = entry.key;
		
		if ( symbols.containsKey(key)){
			symbols.get(key).add(entry.value);
		}else{
			List<SpxSymbol> values = new ArrayList<SpxSymbol>(); 
			values.add(entry.value);
			symbols.put( key , values );
		}
	}
	
	protected static List<SpxSymbol> lookupSymbols(Map<SpxSymbolKey, List<SpxSymbol>> members, IStrategoTerm id){
//		String name = "John Smith"; // For example
//		Map<String, Student> students_ = new HashMap<String, Sandbox.Student>();
//
//		for (Map.Entry<String, Student> entry : students_.entrySet()) {
//		    // If the entry key is "John*", this code will match if name = "John Smith"
//		    if (name.matches("^.*" + entry.getKey().replace("*", ".*") + ".*$")) {
//		        // do something with the matching map entry
//		        System.out.println("Student " + entry.getValue() + " matched " + entry.getKey());
//		    }
//		}
		SpxSymbolKey key = new SpxSymbolKey(id);
		List<SpxSymbol> resolvedSymbols = members.get(key);
		
		return (resolvedSymbols == null) ? new ArrayList<SpxSymbol>() : resolvedSymbols ; 
	}
	
	protected  static SpxSymbol lookupSymbol(Map<SpxSymbolKey, List<SpxSymbol>> members,  IStrategoTerm id , IStrategoTerm type){
		SpxSymbolKey key = new SpxSymbolKey(id);
		List<SpxSymbol> resolvedSymbols = members.get(key);
		
		if(resolvedSymbols != null && resolvedSymbols.size() > 0 ){
			List<SpxSymbol> expectedTypedSymbol  = SpxSymbol.filterByType((IStrategoConstructor)type, resolvedSymbols);
			if(expectedTypedSymbol.size() >0 )
				return expectedTypedSymbol.get(expectedTypedSymbol.size()-1);
		}
		return null;
	}
	
	public SpxSymbol resolve(IStrategoTerm searchingFor, IStrategoTerm type, INamespace searchedBy, SpxSemanticIndexFacade  facade) throws SpxSymbolTableException{
		facade.persistenceManager().logMessage(this.src, "resolve | Resolving Symbol in " + this.namespaceUri().id() +  " . Key :  " + searchingFor + " origin Namespace: " + searchedBy.namespaceUri().id() );
		
		assert type instanceof IStrategoConstructor : "Type is expected to be IStrategoConstructor" ;
			
		SpxSymbol result = lookupSymbol(getMembers(), searchingFor , type);
		if(result!=null)
			return result;
	
		// Symbols could not be found in the current scope
		// Hence, searching any enclosing(parent) scope if it is not 
		// null. After searching global scope, it is not searching
		// anymore.
		INamespace namespace = getEnclosingNamespace(facade.persistenceManager().spxSymbolTable());
		if( namespace  != null) {
			//checks whether searching to the enclosing scope is allowed.
			if( shouldSearchInEnclosingNamespace( searchedBy))
				return namespace.resolve(searchingFor, type, this, facade);
		}	 
		
		return null; // symbol is not found
	}
	
	public Iterable<SpxSymbol> resolveAll(IStrategoTerm searchingFor, INamespace searchedBy, SpxSemanticIndexFacade  facade) throws SpxSymbolTableException {
		
		facade.persistenceManager().logMessage(this.src, "resolveAll(Base) | Resolving Symbol in " + this.namespaceUri().id() +  " . Key :  " + searchingFor + " origin Namespace: " + searchedBy.namespaceUri().id() );
		
		Set<SpxSymbol> retResult = new HashSet<SpxSymbol>();
		
		List<SpxSymbol> lookupResult = lookupSymbols(getMembers() , searchingFor);
		retResult.addAll(lookupResult);
		
		INamespace namespace = getEnclosingNamespace(facade.persistenceManager().spxSymbolTable());
		//checking whether resolved namespace is Null. In that case, all the scopes are covered.
		//also checking that the resolved namespace is not equal to the current namespace 
		//that we already have searched - to avoid any cycle in the hierarchy.
		if( namespace  != null && !namespace.equals(this)){
			//checks whether searching to the enclosing scope is allowed.
			if( shouldSearchInEnclosingNamespace(searchedBy)){	
				Set<SpxSymbol> parentResults  = (Set<SpxSymbol>)namespace.resolveAll(searchingFor, this ,facade);
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
	
	public Map<SpxSymbolKey, List<SpxSymbol>> getMembers(){
		if(symbols == null)
			symbols = new HashMap<SpxSymbolKey, List<SpxSymbol>>();
		
		return this.symbols;}

	public void clear() { if(this.symbols != null) this.symbols.clear();}

	/* 
	 * Returns the enclosing scope of the current scope.
	 * */
	public INamespace getEnclosingNamespace(INamespaceResolver rs) throws SpxSymbolTableException { return (_enclosingNamespaceId != null) ? _enclosingNamespaceId.resolve(rs) : null; }
	
	public INamespace getCurrentNamespace(INamespaceResolver rs) throws SpxSymbolTableException{ return _currentNamespaceId.resolve(rs); }
	
	public boolean isInternalNamespace() { return false;  }
	
	protected boolean shouldSearchInInternalNamespace( INamespace searchedBy) {
		// If searchedBy Namespace is enclosingNamespace of CurrentNamespace 
		// Search for internal symbol scopes as well
		return searchedBy.namespaceUri().equals(this.enclosingNamespaceUri()) || searchedBy.namespaceUri().equals(this.namespaceUri());  
	}
	
	/**
	 * Base Condition of the lookup : 
	 * Allow search enclosing Namespace only if searchedBy is not enclosing Namespace. 
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
		return "namespace : "+ src + "";
	}
}