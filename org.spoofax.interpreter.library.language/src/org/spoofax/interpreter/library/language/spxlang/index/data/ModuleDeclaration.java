package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.util.ArrayList;
import java.util.List;

import org.spoofax.interpreter.library.language.spxlang.index.INamespace;
import org.spoofax.interpreter.library.language.spxlang.index.INamespaceFactory;
import org.spoofax.interpreter.library.language.spxlang.index.ModuleNamespace;
import org.spoofax.interpreter.library.language.spxlang.index.SpxPrimarySymbolTable;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class ModuleDeclaration extends IdentifiableConstruct implements INamespaceFactory  
{
	private static final long serialVersionUID = 1989975037160433882L;

	public static final int ModuleTypedQNameIndex = 0;
	public static final int ModulePathIndex = 1;
	public static final int PackageTypedQNameIndex = 2;
	public static final int AstIndex = 3;
	public static final int AnalyzedAstIndex = 4;
	
	public final String resourceAbsPath; 

	public final IStrategoList enclosingPackageID;
	private long  lastModifiedOn;
	
	
	public ModuleDeclaration(String resourceAbsPath, IStrategoList id , IStrategoList packageID) {
		super(id);
		
		this.resourceAbsPath = resourceAbsPath;
		this.enclosingPackageID = packageID;
	}

	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.IdentifiableConstruct#getFileLocation()
	 */
	@Override 
	protected String getLocation() {
		return resourceAbsPath;
	}


	/**
	 * @param tf
	 * @param decls
	 * @return
	 */
	IStrategoTerm getIndexSummary(ITermFactory tf) {
		
		IStrategoString mId =tf.makeString(this.getIdString()) ;
		IStrategoString mLoc = tf.makeString(this.getLocation());
			
		return tf.makeTuple(mId, mLoc);
	
	}
	
	public static IStrategoList getModuleId(SpxSemanticIndexFacade facade, IStrategoAppl moduleQName ){
		IStrategoConstructor moduleCon  = facade.getCons().getModuleQNameCon();
		
		if(moduleCon   == moduleQName.getConstructor())
			return getID(facade, (IStrategoAppl)moduleQName.getSubterm(ModuleTypedQNameIndex));	
		
		throw new IllegalArgumentException("Invalid module qname : "+ moduleQName.toString());
	}
	
	public static IStrategoAppl toModuleQNameAppl(SpxSemanticIndexFacade facade , ModuleDeclaration decl)
	{
		return toIdTerm(facade, facade.getCons().getModuleQNameCon() , decl.getId());
	}
	
	public static IStrategoAppl toModuleQNameAppl(SpxSemanticIndexFacade facade , IStrategoList id)
	{
		return toIdTerm(facade, facade.getCons().getModuleQNameCon() , id);
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

		IStrategoConstructor moduleDeclCons = idxFacade.getCons().getModuleDeclCon();
		
		IStrategoAppl moduleQNameAppl =toModuleQNameAppl( idxFacade,  this);
		IStrategoString resAbsPathTerm = termFactory.makeString(resourceAbsPath) ;
		IStrategoAppl packageQNameAppl = PackageDeclaration.toPackageQNameAppl(idxFacade, this.enclosingPackageID);
		
		IStrategoTerm retTerm = termFactory.makeAppl(moduleDeclCons,moduleQNameAppl,resAbsPathTerm,packageQNameAppl);
		
		return this.forceImploderAttachment(retTerm);
	}

	public Iterable<INamespace> newNamespaces(SpxSemanticIndexFacade idxFacade) {
		
		List<INamespace> namespaces = new ArrayList<INamespace>();
		
		SpxPrimarySymbolTable  table =  idxFacade.getPersistenceManager().spxSymbolTable() ;
		
		NamespaceUri namespaceUri = table.toNamespaceUri(id) ;
		NamespaceUri packageUri = table.toNamespaceUri(enclosingPackageID) ;
		
		namespaces.add(ModuleNamespace.createInstance(namespaceUri, packageUri,idxFacade, getLocation()));
		
		return namespaces; 
	}
	
	public  NamespaceUri getNamespaceUri(SpxSemanticIndexFacade idxFacade)
	{
		return idxFacade.getPersistenceManager().spxSymbolTable().toNamespaceUri(id) ;
	}

	/**
	 * @return the lastModifiedOn
	 */
	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	/**
	 * @param lastModifiedOn the lastModifiedOn to set
	 */
	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModuleDeclaration [ id= " + id 
				+ ", packageId ="+ enclosingPackageID 
				+ ", resourceAbsPath= " + resourceAbsPath+ "]"
				+ "{ lastModifiedOn= " + lastModifiedOn+ "}";
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
}

