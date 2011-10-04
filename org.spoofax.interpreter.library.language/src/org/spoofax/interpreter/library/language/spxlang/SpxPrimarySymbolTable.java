package org.spoofax.interpreter.library.language.spxlang;

import java.util.Iterator;
import java.util.Set;

import jdbm.PrimaryMap;
import jdbm.SecondaryHashMap;
import jdbm.SecondaryKeyExtractor;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SpxPrimarySymbolTable implements INamespaceResolver{
	
	private final ISpxPersistenceManager _manager; // Persistence Manager
	private final PrimaryMap <NamespaceUri,INamespace> namespaces;
	private final SecondaryHashMap <IStrategoList,NamespaceUri,INamespace> namespaceByStrategoId;
	private transient INamespace _activeNamespace ;
	
	//TODO implement remove package and remove module event handlers
	public SpxPrimarySymbolTable (SpxSemanticIndexFacade facade){
		assert facade != null  : "SpxSemanticIndexFacade  is expected to non-null" ;
		_manager = facade.persistenceManager();

		String tableName = _manager.getProjectName() + "primary_symbol_table.idx";
		
		namespaces  = _manager.loadHashMap(tableName + "namespaces.idx");
		namespaceByStrategoId = namespaces.secondaryHashMap(tableName+ ".namespaceByStrategoId.idx", 
				new SecondaryKeyExtractor<IStrategoList,NamespaceUri,INamespace>()
				{
					public IStrategoList extractSecondaryKey(NamespaceUri k,INamespace v) {
						return k.id(); 
					}
				});
		
		addGlobalNamespace(facade);
	}
	
	/**
	 * Adding Global Namespace in symbol-table by default.
	 * @param facade
	 */
	public void addGlobalNamespace(SpxSemanticIndexFacade facade){
		
		this.defineNamespace(GlobalNamespace.createInstance(facade));
	}
	
	public void defineNamespace(INamespace namespace) {
		// if not already defined, defining this namespace
		if (!containsNamespace(namespace))
			this.namespaces.put(namespace.namespaceUri(), namespace);
	}
	
	NamespaceUri toNamespaceUri(IStrategoList spoofaxId) {
		NamespaceUri uri = getNamespaceUri(spoofaxId);
		if(uri == null) {
			uri = new NamespaceUri(spoofaxId);
		}
		return uri;
	}
	
	public INamespace resolveNamespace(IStrategoList id){
		Iterator<INamespace> resolvedNamespaces = namespaceByStrategoId.getPrimaryValues(id).iterator();
		if(resolvedNamespaces.hasNext())
			return resolvedNamespaces.next();
		else
			return null;
	}
	
	public INamespace resolveNamespace(NamespaceUri id) {
		return namespaces.get(id); 
	}
	
	public NamespaceUri getNamespaceUri(IStrategoList id) {
		Iterable<NamespaceUri> uriIterator = namespaceByStrategoId.get(id);
		if(uriIterator != null)
			for( NamespaceUri uri : uriIterator)
				return uri;
		
		return null;
	}
	
	public boolean containsNamespace(IStrategoList id) { return namespaceByStrategoId.containsKey(id);}
	
	public boolean containsNamespace(NamespaceUri namespaceId) { return namespaces.containsKey(namespaceId);}
	
	public boolean containsNamespace(INamespace namespace) { return this.containsNamespace(namespace.namespaceUri());}

	public void clear(){  namespaces.clear();  }
	
	public int size() { return namespaces.size();}
	 
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxPrimarySymbolTable ( defined namespaces : " + namespaces.keySet() + ")";
	}
	
	public Set<NamespaceUri> getAllNamespaces() { return namespaces.keySet() ; }

	public void defineSymbol(SpxSemanticIndexFacade facade, IStrategoList namespaceId ,  SpxSymbolTableEntry symTableEntry) {
		
		ensureActiveNamespaceLoaded(namespaceId);
	
		_activeNamespace.define(symTableEntry, facade.persistenceManager()); 
	}
	
	private void ensureActiveNamespaceLoaded(IStrategoList namespaceId){
		if(_activeNamespace== null ||!_activeNamespace.namespaceUri().equalSpoofaxId(namespaceId))
			//Keeping a transient reference to the current/active Namespace
			//More likely that there are other symbols to be defined in the
			//current and active namespace. In that case, it will imporve 
			//performance as namespace resolving avoided by means of extra 
			//caching
			_activeNamespace = this.resolveNamespace(namespaceId);
	}
	


	public Iterable<SpxSymbol> resolveSymbols(SpxSemanticIndexFacade spxSemanticIndexFacade, IStrategoList namespaceId, IStrategoTerm symbolId , IStrategoConstructor symbolType) throws SpxSymbolTableException {
		ensureActiveNamespaceLoaded(namespaceId);
		
		return _activeNamespace.resolveAll(symbolId, symbolType ,spxSemanticIndexFacade);
	} 
}



/*
 * Seperate chaning 
 * 
 * Indexed using ID . If multiple symbol is there will return first one 
 * matching type. 
 * 
 * */


/*
 

- symbol FindSymbol( Scope, ID , CTOR )
{
    scope = symbolTable.getActiveScope( scope) 
    //stop search as soon as found atleast one symbol 
}
 
- symbols FindAllSymbols(Scope , ID , CTor) 
{
    scope = symbolTable.getActiveScope( scope) 
    // search symbol in all the visible scope
}
 */