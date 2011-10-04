package org.spoofax.interpreter.library.language.spxlang;

import java.util.ArrayList;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class ModuleDeclaration extends IdentifiableConstruct implements INamespaceFactory  
{
	private static final long serialVersionUID = -6249406731326662111L;

	static final int ModuleTypedQNameIndex = 0;
	static final int ModulePathIndex = 1;
	static final int PackageTypedQNameIndex = 2;
	static final int AstIndex = 3;
	static final int AnalyzedAstIndex = 4;
	
	final String resourceAbsPath; 

	final IStrategoList enclosingPackageID;
	
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.IdentifiableConstruct#getFileLocation()
	 */
	@Override 
	protected String getFileLocation() {
		return resourceAbsPath;
	}

	public ModuleDeclaration(String resourceAbsPath, IStrategoList id , IStrategoList packageID) {
		super(id);
		
		this.resourceAbsPath = resourceAbsPath;
		this.enclosingPackageID = packageID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModuleDeclaration [ id= " + id 
				+ ", packageId ="+ enclosingPackageID 
				+ ", resourceAbsPath= " + resourceAbsPath+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((resourceAbsPath == null) ? 0 : resourceAbsPath.hashCode());
		
		result = prime * result + ((enclosingPackageID == null) ? 0 : enclosingPackageID.hashCode());
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleDeclaration other = (ModuleDeclaration) obj;
		if (resourceAbsPath == null) {
			if (other.resourceAbsPath != null)
				return false;
		} else if (!resourceAbsPath.equals(other.resourceAbsPath))
			return false;
		
		if (enclosingPackageID == null) {
			if (other.enclosingPackageID!= null)
				return false;
		} else if (!enclosingPackageID.match(other.enclosingPackageID))
			return false;
		
		return true;
	}
	
	public static IStrategoList getModuleId(SpxSemanticIndexFacade facade, IStrategoAppl moduleQName )
	{
		IStrategoConstructor moduleCon  = facade.getModuleQNameCon();
		
		if(moduleCon   == moduleQName.getConstructor())
			return getID(facade, (IStrategoAppl)moduleQName.getSubterm(ModuleTypedQNameIndex));	
		
		throw new IllegalArgumentException("Invalid module qname : "+ moduleQName.toString());
	}
	
	public static IStrategoAppl toModuleIdTerm(SpxSemanticIndexFacade facade , ModuleDeclaration decl)
	{
		return toIdTerm(facade, facade.getModuleQNameCon() , decl.getId());
	}

	/* Transforms {@link ModuleDeclaration} to following term : 
	 * 		ModuleDecl :  Module * String * Package -> Def
	 * 
	 * (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.BaseConstructDeclaration#toTerm(org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	@Override
	public IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade) {
		
		ITermFactory termFactory = idxFacade.getTermFactory();

		IStrategoConstructor moduleDeclCons = idxFacade.getModuleDeclCon();
		
		IStrategoAppl moduleQNameAppl =toModuleIdTerm( idxFacade,  this);
		IStrategoString resAbsPathTerm = termFactory.makeString(resourceAbsPath) ;
		IStrategoAppl packageQNameAppl = PackageDeclaration.toPackageIdTerm(idxFacade, this.enclosingPackageID);
		
		IStrategoTerm retTerm = termFactory.makeAppl(moduleDeclCons,moduleQNameAppl,resAbsPathTerm,packageQNameAppl);
		
		return this.forceImploderAttachment(retTerm);
	}

	public Iterable<INamespace> newNamespaces(SpxSemanticIndexFacade idxFacade) {
		
		List<INamespace> namespaces = new ArrayList<INamespace>();
		
		SpxPrimarySymbolTable  table =  idxFacade.persistenceManager().spxSymbolTable() ;
		
		NamespaceUri namespaceUri = table.toNamespaceUri(id) ;
		NamespaceUri packageUri = table.toNamespaceUri(enclosingPackageID) ;
		
		namespaces.add(ModuleNamespace.createInstance(namespaceUri, packageUri,idxFacade));
		
		return namespaces; 
	}
	
	NamespaceUri getNamespaceUri(SpxSemanticIndexFacade idxFacade)
	{
		return idxFacade.persistenceManager().spxSymbolTable().toNamespaceUri(id) ;
	}
}

