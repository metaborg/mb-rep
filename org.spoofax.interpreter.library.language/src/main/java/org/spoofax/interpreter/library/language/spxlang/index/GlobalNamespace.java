package org.spoofax.interpreter.library.language.spxlang.index;

import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public final class GlobalNamespace extends BaseNamespace {
	
	private static final long serialVersionUID = -9194490383911401603L;
	
	private GlobalNamespace(NamespaceUri id , IStrategoConstructor type, ISpxPersistenceManager manager){
		super(id ,type, manager, null);
	}
	
	public IStrategoAppl toTypedQualifiedName(SpxSemanticIndexFacade facade){
		return facade.getTermFactory().makeAppl(facade.getCons().getGlobalNamespaceTypeCon());
	} 
	
	/**
	 * Creates an instance of GlobalScope. 
	 * 
	 * @param facade
	 * @return
	 */
	public static INamespace createInstance(SpxSemanticIndexFacade facade){
		SpxPrimarySymbolTable  symbol_table = facade.getPersistenceManager().spxSymbolTable();
		IStrategoList spoofaxNamespaceUri = getGlobalNamespaceId(facade);
		INamespace gns = symbol_table.resolveNamespace(spoofaxNamespaceUri); 
		if(gns != null)
			return gns;
		else{
			return new GlobalNamespace(symbol_table.toNamespaceUri(spoofaxNamespaceUri), 
					facade.getCons().getGlobalNamespaceTypeCon(), 
					facade.getPersistenceManager()
			);
		}

	}
	
	public static IStrategoList getGlobalNamespaceId(SpxSemanticIndexFacade facade){
		ITermFactory termFactory = facade.getTermFactory();
		
		return termFactory.makeList(facade.getCons().getGlobalNamespaceTypeCon());
	}
}
