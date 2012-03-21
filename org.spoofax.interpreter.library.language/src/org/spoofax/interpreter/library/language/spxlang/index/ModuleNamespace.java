package org.spoofax.interpreter.library.language.spxlang.index;

import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;

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
	 * @param facade
	 * @param moduleFilePath TODO
	 * 
	 * @return
	 */
	public static INamespace createInstance(NamespaceUri id, NamespaceUri enclosingNamespaceId, SpxSemanticIndexFacade facade, String moduleFilePath) {
		SpxPrimarySymbolTable  symbol_table = facade.getPersistenceManager().spxSymbolTable();
		INamespace ns = symbol_table.resolveNamespace(id); 
		if(ns == null)
		{	
			ns = new ModuleNamespace(id, facade.getCons().getModuleNamespaceTypeCon(), enclosingNamespaceId, facade.getPersistenceManager());
			((ModuleNamespace)ns).setAbosoluteFilePath(moduleFilePath);
		}
		return ns;
	}

	@Override
	public IStrategoAppl toTypedQualifiedName(SpxSemanticIndexFacade facade) {
		return ModuleDeclaration.toModuleQNameAppl(facade, this.namespaceUri().strategoID(facade.getTermFactory()));
	}
	
	protected String moduleFilePath;
	
	void setAbosoluteFilePath(String filePath){
		moduleFilePath = filePath;
	}

	@Override
	public String getAbosoluteFilePath(){  
		return moduleFilePath;
	}
}
