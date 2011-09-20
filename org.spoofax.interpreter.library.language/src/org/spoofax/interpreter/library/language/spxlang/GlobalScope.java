package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class GlobalScope extends BaseScope {
	
	private static final long serialVersionUID = -9194490383911401603L;
	private static final String CTOR_NAME = "Global" ;
	
	public GlobalScope(NamespaceId id , IStrategoConstructor type, ISpxPersistenceManager manager){
		super(id ,type, null, manager);
	}
	
	/**
	 * Creates an instance of GlobalScope. Point of caution : GlobalScope is valid  
	 * for the current project. 
	 * 
	 * @param facade
	 * @return
	 */
	public static INamespace createInstance(SpxSemanticIndexFacade facade) 
	{
		ITermFactory termFactory = facade.getTermFactory();

		NamespaceId id = new NamespaceId(getGlobalScopeId(facade));
		
		GlobalScope scope = new GlobalScope(id, 
											termFactory.makeConstructor(CTOR_NAME, 0), 
											facade.getPersistenceManager()
											);
		return scope;
	}
	
	
	static IStrategoList getGlobalScopeId(SpxSemanticIndexFacade facade){
		ITermFactory termFactory = facade.getTermFactory();
		
		return termFactory.makeList(termFactory.makeString(CTOR_NAME));
	}
}
