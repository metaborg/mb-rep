package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public final class GlobalNamespace extends BaseNamespace {
	
	private static final long serialVersionUID = -9194490383911401603L;
	
	public GlobalNamespace(NamespaceUri id , IStrategoConstructor type, ISpxPersistenceManager manager){
		super(id ,type, manager, null);
	}
	
	
	/**
	 * Creates an instance of GlobalScope. 
	 * 
	 * @param facade
	 * @return
	 */
	public static INamespace createInstance(SpxSemanticIndexFacade facade){
		SpxPrimarySymbolTable  symbol_table = facade.persistenceManager().spxSymbolTable();
		IStrategoList spoofaxNamespaceUri = getGlobalNamespaceId(facade);
		
		NamespaceUri uri; 
		if ( symbol_table == null )
			uri  = new NamespaceUri(spoofaxNamespaceUri);
		else
			uri =  symbol_table.toNamespaceUri(spoofaxNamespaceUri); 

		return new GlobalNamespace(uri, 
				facade.getGlobalNamespaceTypeCon(), 
				facade.persistenceManager()
		);

	}
	
	public static IStrategoList getGlobalNamespaceId(SpxSemanticIndexFacade facade){
		ITermFactory termFactory = facade.getTermFactory();
		
		return termFactory.makeList(facade.getGlobalNamespaceTypeCon());
	}
}
