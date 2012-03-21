package org.spoofax.interpreter.library.language.spxlang.index;

import java.util.UUID;

import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public final class LocalNamespace extends BaseNamespace{
	private static final long serialVersionUID = 5558956449728735306L;

	
	private LocalNamespace(NamespaceUri currentNamespace,IStrategoConstructor type, ISpxPersistenceManager manager,NamespaceUri enclosingNamespace) {
		super(currentNamespace, type, manager, enclosingNamespace);
	
	}


	
	@Override
	public IStrategoAppl toTypedQualifiedName(SpxSemanticIndexFacade facade) {
		return facade.getTermFactory().makeAppl(facade.getCons().getLocalNamespaceTypeCon());
	}
	
	/**
	 * Creates an instance of GlobalScope. Point of caution : GlobalScope is valid  
	 * for the current project. 
	 * 
	 * @param facade
	 * @return
	 */
	public static INamespace createInstance(SpxSemanticIndexFacade facade,INamespace enclosingNamespace) {
		
		ITermFactory termFactory = facade.getTermFactory();
		
		UUID uniqueId= UUID.randomUUID();
		NamespaceUri localNamespaceUri = new NamespaceUri(termFactory.makeList(termFactory.makeString("anonymous_"+uniqueId.toString())));

		return new LocalNamespace(localNamespaceUri , facade.getCons().getLocalNamespaceTypeCon(), facade.getPersistenceManager(), enclosingNamespace.namespaceUri());
	}


	public static IStrategoList getLocalNamespaceId(IStrategoTerm localAnonymousId) {

		return (IStrategoList) localAnonymousId;
	}
	
	@Override
	public String getAbosoluteFilePath(){
		return super.getAbosoluteFilePath();
	}
}
