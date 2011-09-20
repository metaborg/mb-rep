package org.spoofax.interpreter.library.language.spxlang;

import jdbm.PrimaryMap;
import jdbm.SecondaryHashMap;
import jdbm.SecondaryKeyExtractor;

import org.spoofax.interpreter.terms.IStrategoList;

public class SpxPrimarySymbolTable implements INamespaceResolver{
	
	private final ISpxPersistenceManager _manager; // Persistence Manager
	
	private final PrimaryMap <NamespaceId,INamespace> namespaces;
	private final SecondaryHashMap <IStrategoList,NamespaceId,INamespace> namespaceByStrategoId;
	
	public SpxPrimarySymbolTable (String tableName, ISpxPersistenceManager manager){
		_manager = manager;
		
		namespaces  = _manager.loadHashMap(tableName + "namespaces.idx");
		
		namespaceByStrategoId = namespaces.secondaryHashMap(tableName+ ".namespaceByStrategoId.idx", 
				new SecondaryKeyExtractor<IStrategoList,NamespaceId,INamespace>()
				{
					public IStrategoList extractSecondaryKey(NamespaceId k,INamespace v) {
						return k.ID(); 
					}
				});
	}
	
	public Iterable<INamespace> getNamespaces(IStrategoList id){
		return namespaceByStrategoId.getPrimaryValues(id);
	}
	
	
	public INamespace resolveScope(NamespaceId id) {
		return namespaces.get(id); 
	}
	
	public void define(IStrategoList namespaceId , SpxSymbol symbol)
	{
		
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
 
// Ctor x Scope x ID x Symbol -> Definition 
symbol : Term x  Term x Term x Term -> def 

- define symbol 
{
    scope = symbolTable.getActiveScope( scope) 
    scope.defineSymbol ( Ctor , ID , Symbol) ;

}

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