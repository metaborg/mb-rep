package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * Instantiates a new ModuleNamespace . Each ModuleNamespace is associated with a ModuleDeclaration.
 *  
 * NOTE: Internal ModuleNamespaces does not have any physical ModuleDeclaration associated.    
 */
public final class ModuleNamespace extends BaseNamespace
{
	private static final long serialVersionUID = 5255913747644738988L;
	/**
	 * A Flag to refer to the Internal Namespace of the enclosing Namespace. 
	 * When it is set to true, only way to access the enclosed symbol is through 
	 * this Namespace or the Enclosing PackageNamespace. Other 
	 * ModuleNamespace cannot resolve any symbols of this internal  Namespace.
	 */
	boolean isInternalNamespace = false;   
	
	private ModuleNamespace(NamespaceUri currentNamespaceUri, IStrategoConstructor namespaceType ,NamespaceUri enclosingNamespaceUri,  ISpxPersistenceManager manager) {
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
		SpxPrimarySymbolTable  symbol_table = facade.persistenceManager().spxSymbolTable();
		INamespace ns = symbol_table.resolveNamespace(id); 
		if(ns != null)
			return ns;
		else
			return new ModuleNamespace(id, facade.getModuleNamespaceTypeCon(), enclosingNamespaceId, facade.persistenceManager());
	}

	@Override
	public IStrategoAppl toTypedQualifiedName(SpxSemanticIndexFacade facade) {
		return ModuleDeclaration.toModuleQNameAppl(facade, this.namespaceUri().id());
	}
	
	
}
