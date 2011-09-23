package org.spoofax.interpreter.library.language.spxlang;

import java.util.HashSet;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class PackageDeclaration extends IdentifiableConstruct
{
	private static final long serialVersionUID = -9081890582103567413L;
	
	
	static final int PACKAGE_ID_INDEX = 0;
	static final int SPX_COMPILATION_UNIT_PATH = 1;
	
	final Set<String> resourceAbsPaths = new HashSet<String>();
	final Set<IStrategoList> importedScopes  =  new HashSet<IStrategoList>();
	
	
	PackageDeclaration(IStrategoList id)
	{
		super(id);
	}
	
	public PackageDeclaration(String resourceAbsPath, IStrategoList id) {
		super(id);
		
		resourceAbsPaths.add(resourceAbsPath); 
	}
	
	public void add(String resAbsolutePath)
	{
		resourceAbsPaths.add(resAbsolutePath);
	}
	
	public void remove(String resAbsolutePath)
	{
		resourceAbsPaths.remove(resAbsolutePath);
	}
	
	public Set<String> getAllFilePaths()
	{
		return resourceAbsPaths;
	}
	
	public boolean doesNotExistInAnyFile()
	{
		return (resourceAbsPaths == null) || (resourceAbsPaths.size() == 0) ; 
	}
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.BaseConstructDeclaration#toTerm(org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	@Override
	public IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade) {
		ITermFactory termFactory = idxFacade.getTermFactory();
		
		IStrategoConstructor packageDeclCons = idxFacade.getPackageDeclCon();
		IStrategoList absPathList = termFactory.makeList();
		
		for(String resourceAbsPath : resourceAbsPaths)
		{
			IStrategoString absPathTerm = termFactory.makeString(resourceAbsPath);
			absPathList  = termFactory.makeListCons(absPathTerm, absPathList);
		}	
		
		IStrategoTerm retTerm = termFactory.makeAppl(
				packageDeclCons, 
				toPackageIdTerm(idxFacade, this),
				absPathList 
		);
		
		return this.forceImploderAttachment(retTerm);
	}
	
	/**
	 * Gets PackageID from Typed Package QName
	 * @param fac
	 * @param packageQName
	 * @return
	 */
	public static IStrategoList getPackageId(SpxSemanticIndexFacade facade,IStrategoAppl packageQName)
	{
		final IStrategoConstructor packageQNameCon = facade.getPackageQNameCon();
		
		if(packageQNameCon == packageQName.getConstructor())
		{
			return getID( facade, (IStrategoAppl)packageQName.getSubterm(0));	
		}
		
		throw new IllegalArgumentException("Invalid Package Typed QName : "+ packageQName);
	}
	
	/**
	 * Converts to typed Package Qualified Name
	 *  
	 * @param termFactory
	 * @param decl
	 * @return
	 */
	public static IStrategoAppl toPackageIdTerm (SpxSemanticIndexFacade facade, PackageDeclaration decl)
	{
		return toPackageIdTerm (facade, decl.getId());
	}
	
	public static IStrategoAppl toPackageIdTerm (SpxSemanticIndexFacade facade, IStrategoList id)
	{
		return toIdTerm(facade ,  facade.getPackageQNameCon(), id);
	}
	
	/**
	 * Creates a new instance of {@link PackageDeclaration} . It acts as a copy
	 * constructor to clone {@code decl}.
	 * 
	 * @param decl an instance of {@link PackageDeclaration} to copy from. 
	 * @return {@link PackageDeclaration}
	 */
	public static PackageDeclaration newInstance( PackageDeclaration decl){
		if (decl == null) 
			return decl;
		
		PackageDeclaration newDecl = new PackageDeclaration(decl.getId());
		for( String str : decl.getAllFilePaths())
		{
			newDecl.add(str); 
		}
		return newDecl;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PackageDeclaration [id=" + id + ", resourceAbsPaths=" + resourceAbsPaths + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((resourceAbsPaths == null) ? 0 : resourceAbsPaths.hashCode());
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
		PackageDeclaration other = (PackageDeclaration) obj;
		if (resourceAbsPaths == null) {
			if (other.resourceAbsPaths != null)
				return false;
		} else if (!resourceAbsPaths.equals(other.resourceAbsPaths))
			return false;
		return true;
	}
}