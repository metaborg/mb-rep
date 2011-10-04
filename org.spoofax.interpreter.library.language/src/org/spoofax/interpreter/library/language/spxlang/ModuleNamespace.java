package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * Instantiates a new ModuleNamespace . Each ModuleNamespace is associated with a ModuleDeclaration.
 *  
 * NOTE: Internal ModuleNamespaces does not have any physical ModuleDeclaration associated.    
 */
public class ModuleNamespace extends BaseNamespace
{
	private static final long serialVersionUID = 5255913747644738988L;
	
	boolean isInternalNamespace = false;
	
	public ModuleNamespace(NamespaceUri currentNamespaceUri, IStrategoConstructor namespaceType ,NamespaceUri enclosingNamespaceUri,  ISpxPersistenceManager manager) {
		super(currentNamespaceUri, namespaceType, manager, enclosingNamespaceUri);
	}

	@Override 
	public boolean isInternalNamespace() { return isInternalNamespace; }; 

	/**
	 * Creates an instance of GlobalScope. Point of caution : GlobalScope is valid  
	 * for the current project. 
	 * 
	 * @param facade
	 * @return
	 */
	public static INamespace createInstance(NamespaceUri id, NamespaceUri enclosingNamespaceId, SpxSemanticIndexFacade facade) {
	
		return new ModuleNamespace(id, facade.getModuleNamespaceTypeCon(), enclosingNamespaceId, facade.persistenceManager());
	}
	
	
}
