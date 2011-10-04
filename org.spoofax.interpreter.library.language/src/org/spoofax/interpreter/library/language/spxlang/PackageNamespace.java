package org.spoofax.interpreter.library.language.spxlang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;


/**
 * Implementation of a PackageNamesapce. 
 * @author Md. Adil Akhter
 */
public class PackageNamespace  extends BaseNamespace {

	private static final long serialVersionUID = 7324156752002137217L;
	private static final String INTERNAL_NAMESPACENAME = "__internal";
	
	// Not serializing it to disk since we already have this information in SpxSemanticIndex
	private transient Set<NamespaceUri> importedNamespaceUris;
	private transient Set<NamespaceUri> enclosedNamespaceUris;
	
	private PackageNamespace(NamespaceUri currentNamespace, IStrategoConstructor type, NamespaceUri enclosingNamespace, ISpxPersistenceManager manager) {
		super(currentNamespace, type, manager, enclosingNamespace);
	}
	
	private void ensureEnclosedNamespaceUrisLoaded(SpxSemanticIndexFacade facade) throws SpxSymbolTableException{
		if( enclosedNamespaceUris == null){
			enclosedNamespaceUris = new HashSet<NamespaceUri>();
			
			Iterable<ModuleDeclaration> mDecls = facade.getModuleDeclarations( this.namespaceUri().id());
			if(mDecls !=null){
				for(ModuleDeclaration m : mDecls){
					enclosedNamespaceUris.add(m.getNamespaceUri(facade));
				}	
			}
		}
	}
	
	private void ensureImportedNamespaceUrisLoaded(SpxSemanticIndexFacade facade) throws SpxSymbolTableException{
		if( importedNamespaceUris == null){
			
			importedNamespaceUris= new HashSet<NamespaceUri>();
			
			SpxPrimarySymbolTable symTable =  facade.persistenceManager().spxSymbolTable();
			
			//getting the package declaration and retrieving it imported references 
			PackageDeclaration decl = facade.lookupPackageDecl(this.namespaceUri().id());
			Iterable<IStrategoList> importedIds = decl.getImportReferneces();
			
			for(IStrategoList l : importedIds){
				importedNamespaceUris.add(symTable.toNamespaceUri(l));; 
			}	
		}
	}
	/* Resolving symbol in PackageNamespace following few   
	 * basic rules. First it try to resolve symbol in its enclosed namespace , then it try to resolve 
	 * the symbol in its enclosing namespace and at last, it looks for the symbol in the imported 
	 * namespaces.
	 * 
	 * @see org.spoofax.interpreter.library.language.spxlang.BaseNamespace#resolve(org.spoofax.interpreter.terms.IStrategoTerm, org.spoofax.interpreter.terms.IStrategoTerm, org.spoofax.interpreter.library.language.spxlang.INamespace, org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	@Override
	public SpxSymbol resolve(IStrategoTerm id, IStrategoTerm type, INamespace searchedBy, SpxSemanticIndexFacade facade) throws SpxSymbolTableException {
		
		ensureEnclosedNamespaceUrisLoaded(facade);
		SpxSymbol retSymbol = resolveSymbolinNamespaces(this.enclosedNamespaceUris, id, type, searchedBy, facade);

		if (retSymbol == null) {
			// Searching in package's local symbols. If not found, search
			// in the enclosing namespaces i.e. in Global Namespace
			retSymbol = super.resolve(id, type, this, facade);

			if (retSymbol == null) {
				// try to resolve in the imported namespaces
				ensureImportedNamespaceUrisLoaded(facade);
				retSymbol = resolveSymbolinNamespaces(this.importedNamespaceUris, id, type, searchedBy, facade);

			}
		}
		return retSymbol;
	}

	@Override
	public Iterable<SpxSymbol> resolveAll(IStrategoTerm key,INamespace originNamespace, SpxSemanticIndexFacade facade) throws SpxSymbolTableException{
		Set<SpxSymbol> retResult = new HashSet<SpxSymbol>();
		
		retResult.addAll((Set<SpxSymbol>)super.resolveAll(key, this, facade));
		
		ensureEnclosedNamespaceUrisLoaded(facade);
		retResult.addAll((Set<SpxSymbol>)resolveAllSymbolsInNamespaces(this.enclosedNamespaceUris, key, originNamespace, facade)) ;
		
		ensureImportedNamespaceUrisLoaded(facade);
		retResult.addAll((Set<SpxSymbol>)resolveAllSymbolsInNamespaces(this.importedNamespaceUris, key, originNamespace, facade)) ;
		
		return retResult;
	}
	
	
	/**
	 * Creates an instance of PackageScope. Also creates internal symbol scopes
	 * 
	 * @param facade
	 * @return
	 */
	public static Iterable<INamespace> createInstances(IStrategoList id, SpxSemanticIndexFacade facade){
		
		SpxPrimarySymbolTable  table =  facade.persistenceManager().spxSymbolTable() ;
		
		NamespaceUri globalNsUri =  table.toNamespaceUri(GlobalNamespace.getGlobalNamespaceId(facade));
		NamespaceUri currentPackageUri = table.toNamespaceUri(id);
	
		List<INamespace> namespaces = new ArrayList<INamespace>();
		PackageNamespace ns = new PackageNamespace(currentPackageUri, facade.getPackageNamespaceTypeCon(), globalNsUri,facade.persistenceManager());
		
		namespaces.add(ns);
		namespaces.add(createInternalNamespace(currentPackageUri , facade));
		
		return namespaces;
	}
	
	
	/**
	 * @param key
	 * @param type
	 * @param searchedOrigin
	 * @param facade
	 * @param retSymbol
	 * @param namespaceResolver
	 * @throws SpxSymbolTableException 
	 */
	private SpxSymbol resolveSymbolinNamespaces(Iterable<NamespaceUri> resolvableUris  ,IStrategoTerm key, IStrategoTerm type, INamespace searchedOrigin, SpxSemanticIndexFacade facade) throws SpxSymbolTableException {
		
		SpxSymbol retSymbol = null;
		INamespaceResolver namespaceResolver = facade.persistenceManager().spxSymbolTable();
		 
		for( NamespaceUri uri : resolvableUris){

			INamespace thisNamespace = uri.resolve(namespaceResolver);
			
			if (disallowLookupIn(thisNamespace , searchedOrigin)){	
				// Current Namespace is an internal Namespace and
				// Internal Namespace Search is disabled for searchedBy 
				// hence, ignoring it.
				continue;
			}
			
			retSymbol = thisNamespace.resolve(key, type, this, facade) ;
			if(retSymbol != null)
				break;
		}
		
		return retSymbol;
	}
	
	private Set<SpxSymbol> resolveAllSymbolsInNamespaces(Iterable<NamespaceUri> resolvableUris  ,IStrategoTerm key, INamespace searchOrigin, SpxSemanticIndexFacade facade) throws SpxSymbolTableException {
		
		Set<SpxSymbol> retSymbol = new HashSet<SpxSymbol>();
		INamespaceResolver namespaceResolver = facade.persistenceManager().spxSymbolTable();
		 
		for( NamespaceUri uri : resolvableUris){

			INamespace thisNamespace = uri.resolve(namespaceResolver);
			
			if (disallowLookupIn(thisNamespace , searchOrigin)){	
				// Current Namespace is an internal Namespace and
				// Internal Namespace Search is disabled for searchedBy 
				// hence, ignoring it.
				continue;
			}
			retSymbol.addAll((Set<SpxSymbol>)thisNamespace.resolveAll(key, searchOrigin, facade));
		}
		
		return retSymbol;
	}
	
	private boolean disallowLookupIn( INamespace namespace , INamespace originNamespace)
	{
		boolean resolveInCurrentNamespaceIsNotAllowed = namespace.isInternalNamespace() && !shouldSearchInInternalNamespace(originNamespace) ;
		boolean currentNamespaceIsSearchedOrigin = namespace.namespaceUri() == originNamespace.namespaceUri();  // disallowing repeatative resolve of the namespace from where search originated.
		
		return resolveInCurrentNamespaceIsNotAllowed || currentNamespaceIsSearchedOrigin;
	
	}
	
	/**
	 * Creates a new Internal namespace for the current package namespace.
	 * @param enclosingNamespaceId
	 * @param idxFacade
	 * @return
	 */
	private static INamespace createInternalNamespace( NamespaceUri enclosingNamespaceId , SpxSemanticIndexFacade idxFacade)
	{
		NamespaceUri internalNamespaceUri = packageInternalNamespace(
				enclosingNamespaceId, idxFacade);
		
		//termFactory.makeList(spoofaxNamespaceId.getAllSubterms() , "");
		ModuleNamespace internalNamespace = (ModuleNamespace)ModuleNamespace.createInstance(internalNamespaceUri, enclosingNamespaceId, idxFacade);
		internalNamespace.isInternalNamespace = true;
		
		return internalNamespace;
	}


	/**
	 * @param enclosingNamespaceId
	 * @param idxFacade
	 * @return
	 */
	public static NamespaceUri packageInternalNamespace(
			NamespaceUri enclosingNamespaceId, SpxSemanticIndexFacade idxFacade) {
		
		ITermFactory termFactory =  idxFacade.getTermFactory();
		SpxPrimarySymbolTable  table =  idxFacade.persistenceManager().spxSymbolTable() ;
		
		List<IStrategoTerm> subTerms = new ArrayList<IStrategoTerm>();
		
		IStrategoList spoofaxNamespaceId = enclosingNamespaceId.id() ;
		
		subTerms.addAll(Arrays.asList(spoofaxNamespaceId.getAllSubterms()));
		subTerms.add(termFactory.makeString(INTERNAL_NAMESPACENAME));
		
		IStrategoList internalModuleID  = termFactory.makeList(subTerms);
		NamespaceUri internalModuleUri  = table.toNamespaceUri(internalModuleID);
		return internalModuleUri;
	}
}