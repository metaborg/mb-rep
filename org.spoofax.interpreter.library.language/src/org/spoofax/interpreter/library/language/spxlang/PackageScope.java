package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class PackageScope  extends BaseScope {

	private static final String CTOR_NAME = "Package" ;
	private static final long serialVersionUID = 7324156752002137217L;
	
	public PackageScope(NamespaceId currentNS, IStrategoConstructor type, NamespaceId enclosingNS, ISpxPersistenceManager manager) {
		super(currentNS, type, enclosingNS, manager);
	}
	
	
	@Override
	public SpxSymbol resolve(IStrategoTerm id, IStrategoTerm type,INamespaceResolver nsResolver, ISpxPersistenceManager logger) 
	{
		return null;
	};
	
	
	
	@Override
	public Iterable<SpxSymbol> resolveAll(IStrategoTerm id,INamespaceResolver nsResolver, ISpxPersistenceManager manager) 
	{
		return null;
	};
	
	
	
	/**
	 * Creates an instance of GlobalScope. Point of caution : GlobalScope is valid  
	 * for the current project. 
	 * 
	 * @param facade
	 * @return
	 */
	public static INamespace createInstance(NamespaceId id, NamespaceId enclosingNsId, SpxSemanticIndexFacade facade) 
	{
		ITermFactory termFactory = facade.getTermFactory();
		
		PackageScope scope = new PackageScope(id, 
											termFactory.makeConstructor(CTOR_NAME, 1),
											enclosingNsId,
											facade.getPersistenceManager()
											);
		return scope;
	}
	
	
}