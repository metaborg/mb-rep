package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Md. Adil Akhter
 * Created On : Aug 21, 2011
 */

class ModuleScope extends BaseScope
{
	private static final String CTOR_NAME = "Module" ;
	private static final long serialVersionUID = 5255913747644738988L;
	
	public ModuleScope(NamespaceId currentNS, IStrategoConstructor type,NamespaceId enclosingNS, ISpxPersistenceManager manager) {
		super(currentNS, type, enclosingNS, manager);
		
	}
	
	
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
