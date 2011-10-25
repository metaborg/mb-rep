

package org.spoofax.interpreter.library.language.spxlang.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbol;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolKey;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableEntry;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
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
	public INamespace define(SpxSymbolTableEntry entry, SpxSemanticIndexFacade f){
		entry.value.setNamespace(_currentNamespaceId);
		
		defineSymbol(entry);
		
		//f.persistenceManager().spxSymbolTable().commit();
		return this;
	}
	public Set<SpxSymbol> undefineSymbols(IStrategoTerm searchingFor, IStrategoTerm type , SpxSemanticIndexFacade  facade){
		SpxSymbolKey key = new SpxSymbolKey(searchingFor);
		Set<SpxSymbol> undefinedSymbols = new HashSet<SpxSymbol>();
		
		if(this.symbols.containsKey(key)){
			// Found following symbols indexed by key 
			List<SpxSymbol> foundSymbols  = getMembers().get(key);
			
			// filtering symbols by type to retrieve the list of symbols to undefine
			List<SpxSymbol> symbolsToDelete  = SpxSymbol.filterByType((IStrategoConstructor)type, foundSymbols);
			
			// deleting the symbols to be undefined
			for ( SpxSymbol s : symbolsToDelete){
				if(foundSymbols.remove(s)){
					// adding to the list of removed symbols to return
					undefinedSymbols.add(s);	
				} 
			}
			this.symbols.put(key, foundSymbols);
		}
		
		//facade.persistenceManager().spxSymbolTable().commit();
		return undefinedSymbols;
	}
	/**
	 * Defines symbol in this Namespace. Define does not replace  
	 * old symbol mapped using the key with the new one. It just adds the 
	 * new symbol at the end of the multi-value hashmap. 
	 * 
	 * @param key - The key that the symbol will be mapped to .
	 * @param symbol - The symbol to store. 
	 */
	private void defineSymbol(SpxSymbolTableEntry entry){
		SpxSymbolKey key = entry.key;
		
		if (symbols.containsKey(key)){
			symbols.get(key).add(entry.value);
		}else{
			List<SpxSymbol> values = new ArrayList<SpxSymbol>(); 
			values.add(entry.value);
			symbols.put( key , values );
		}
	}
	private static List<SpxSymbol> appendSymbols( List<SpxSymbol> origin , List<SpxSymbol> symbols){
		if(symbols != null){
			origin.addAll(symbols);
		}
		return origin;
	}
	
	/**
	 * Returns all the symbols matching the search-criteria specified as the argument. In addition,
	 * it also accepts *(ALL) as an argument and returns all the symbols of a particular type  
	 * from this namespace and the namespace visible in this namespace. 
	 * 
	 * @param members Symbol-Table contains all the members/symbols of this current Namespace
	 * @param id  Symbol-ID
	 * @param type Type of the Symbol we are looking for
	 * @return a List of Symbols matching with the Search Criteria
	 */
	protected static List<SpxSymbol> lookupSymbols(Map<SpxSymbolKey, List<SpxSymbol>> members, IStrategoTerm id, IStrategoTerm type){
		assert type instanceof IStrategoConstructor : "type is expected to be a IStrategoConstructor" ;
	
		SpxSymbolKey key = new SpxSymbolKey(id);
		List<SpxSymbol> resolvedSymbols  = new ArrayList<SpxSymbol>();
		
		if(key.getId().equalsIgnoreCase(Utils.All_SYMBOLS)){
			// Found * in the ID. 
			// Hence returning ALL symbols of  a particular type specified in the argument.
			for (Entry<SpxSymbolKey, List<SpxSymbol>> entry : members.entrySet()) {
				resolvedSymbols = appendSymbols(resolvedSymbols, SpxSymbol.filterByType((IStrategoConstructor)type, members.get(entry.getKey())));
			}
		}
		else{
			resolvedSymbols = appendSymbols(resolvedSymbols, SpxSymbol.filterByType( (IStrategoConstructor)type, members.get(key)));
		}
		
		return resolvedSymbols ; 
	}
	
	protected  static SpxSymbol lookupSymbol(Map<SpxSymbolKey, List<SpxSymbol>> members,  IStrategoTerm id , IStrategoTerm type){
		List<SpxSymbol> resolvedSymbols = lookupSymbols( members , id , type); 
	 	if(resolvedSymbols.size() >0 )
			return resolvedSymbols.get(resolvedSymbols.size()-1);
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
	
	public Set<SpxSymbol> resolveAll(IStrategoTerm searchingFor, IStrategoTerm ofType, INamespace searchedBy, SpxSemanticIndexFacade  facade) throws SpxSymbolTableException {
		
		facade.persistenceManager().logMessage(this.src, "resolveAll(Base) | Resolving Symbol in " + this.namespaceUri().id() +  " . Key :  " + searchingFor + " origin Namespace: " + searchedBy.namespaceUri().id() );
		
		Set<SpxSymbol> retResult = new HashSet<SpxSymbol>();
		
		List<SpxSymbol> lookupResult = lookupSymbols(getMembers() , searchingFor , ofType);
		retResult.addAll(lookupResult);
		
		INamespace namespace = getEnclosingNamespace(facade.persistenceManager().spxSymbolTable());
		//checking whether resolved namespace is Null. In that case, all the scopes are covered.
		//also checking that the resolved namespace is not equal to the current namespace 
		//that we already have searched - to avoid any cycle in the hierarchy.
		if( namespace  != null && !namespace.equals(this)){
			//checks whether searching to the enclosing scope is allowed.
			if( shouldSearchInEnclosingNamespace(searchedBy)){	
				Set<SpxSymbol> parentResults  = (Set<SpxSymbol>)namespace.resolveAll(searchingFor, ofType, this ,facade);
				retResult.addAll(parentResults);
			}
		}	 
		
		return retResult;
		//return SpxSymbol.filterByType((IStrategoConstructor) type, this.resolveAll(id, searchedBy, spxFacade)) ;
	}
	
	
	/* Resolving symbol for given type and symbol id .
	 * 
	 * (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.INamespace#resolveAll(org.spoofax.interpreter.terms.IStrategoTerm, org.spoofax.interpreter.terms.IStrategoTerm, org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	public Set<SpxSymbol> resolveAll(IStrategoTerm searchingFor, IStrategoTerm ofType, SpxSemanticIndexFacade spxFacade) throws SpxSymbolTableException{
		return resolveAll(searchingFor, ofType,  this, spxFacade);
	}
	
	public Map<SpxSymbolKey, List<SpxSymbol>> getMembers(){
		if(symbols == null)
			symbols = new HashMap<SpxSymbolKey, List<SpxSymbol>>();
		
		return this.symbols;
	}

	public void clear() { 
		if(this.symbols != null) 
			this.symbols.clear();
	}

	/* 
	 * Returns the enclosing scope of the current scope.
	 * */
	public INamespace getEnclosingNamespace(INamespaceResolver rs) throws SpxSymbolTableException { 
		return (_enclosingNamespaceId != null) 
					? _enclosingNamespaceId.resolve(rs) 
					: null; 
	}
	
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