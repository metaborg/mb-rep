package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class ModuleDeclaration extends BaseConstructDeclaration 
{
	private static final long serialVersionUID = -6249406731326662111L;
	static final String _moduleIdContructorName = "Module";
	
	static final int ModuleTypedQNameIndex = 0;
	static final int ModulePathIndex = 1;
	static final int PackageTypedQNameIndex = 2;
	static final int AstIndex = 3;
	static final int AnalyzedAstIndex = 3;
	
	final String resourceAbsPath; 
	
	@Override protected String getFileLocation() {
		return resourceAbsPath;
	}

	final IStrategoList enclosingPackageID;
	
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
	
	public static IStrategoList getModuleId(ITermFactory fac, IStrategoAppl moduleQName )
	{
		final IStrategoConstructor moduleQNameCons = fac.makeConstructor(_moduleIdContructorName, 1);
		
		if(moduleQNameCons == moduleQName.getConstructor())
		{
			return (IStrategoList)moduleQName.getSubterm(0);
		}
		else
			throw new IllegalArgumentException("Invalid module qname : "+ moduleQName.toString());
	}
	
	public static IStrategoAppl toModuleIdTerm(ITermFactory termFactory , ModuleDeclaration decl)
	{
		return toIdTerm(termFactory, _moduleIdContructorName, decl.getId());
	}

	/* Transforms {@link ModuleDeclaration} to following term-
	 * 		ModuleDecl :  Module * String * Package -> Def
	 * 
	 * (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.BaseConstructDeclaration#toTerm(org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	@Override
	public IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade) {
		
		ITermFactory termFactory = idxFacade.getTermFactory();

		IStrategoConstructor moduleDeclCons = idxFacade.getModuleDeclCon();
		
		IStrategoAppl moduleQNameAppl =toModuleIdTerm( termFactory ,  this);
		IStrategoString resAbsPathTerm = termFactory.makeString(resourceAbsPath) ;
		IStrategoAppl packageQNameAppl = PackageDeclaration.toPackageIdTerm(termFactory, this.enclosingPackageID);
		
		IStrategoTerm retTerm = termFactory.makeAppl(moduleDeclCons,moduleQNameAppl,resAbsPathTerm,packageQNameAppl);
		
		return this.forceImploderAttachment(retTerm);
	}
}

