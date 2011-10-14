package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spoofax.interpreter.library.language.spxlang.index.INamespace;
import org.spoofax.interpreter.library.language.spxlang.index.INamespaceFactory;
import org.spoofax.interpreter.library.language.spxlang.index.PackageNamespace;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class PackageDeclaration extends IdentifiableConstruct implements INamespaceFactory
{
	private static final long serialVersionUID = -9081890582103567413L;
	
	public static final int PACKAGE_ID_INDEX = 0;
	public static final int SPX_COMPILATION_UNIT_PATH = 1;
	
	private final Set<String> resourceAbsPaths = new HashSet<String>();
	private final Set<IStrategoList> importedToReferences = new HashSet<IStrategoList>();
	
	public PackageDeclaration(String resourceAbsPath, IStrategoList id) {
		super(id);
		
		resourceAbsPaths.add(resourceAbsPath); 
	}
	
	/**
	 * Initializes an instance of {@link PackageDeclaration}
	 * 
	 * @param id
	 */
	PackageDeclaration(IStrategoList id){
		super(id);
	}
	
	public Set<IStrategoList> getImortedToPackageReferences(){return importedToReferences;}
	
	public void removeImportedToPackageReference(PackageDeclaration decl) {
		this.importedToReferences.remove(decl.getId());
	}
	
	public void addFileUri(String resAbsolutePath){
		resourceAbsPaths.add(resAbsolutePath);
	}
	
	public void addImportedTo(IStrategoList packageId){
		importedToReferences.add(packageId);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj){
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
	
	public Set<String> getAllFilePaths(){
		return resourceAbsPaths;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((resourceAbsPaths == null) ? 0 : resourceAbsPaths.hashCode());
		return result;
	}
	
	public boolean isNotExistedInAnyFile(){
		return (resourceAbsPaths == null) || (resourceAbsPaths.size() == 0) ; 
	}
	
	public void removeFileUri(String resAbsolutePath){resourceAbsPaths.remove(resAbsolutePath);}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PackageDeclaration [id=" + id + ", resourceAbsPaths=" + resourceAbsPaths + "]";
	}
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.BaseConstructDeclaration#toTerm(org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	@Override
	public IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade){
		ITermFactory termFactory = idxFacade.getTermFactory();
		
		IStrategoConstructor packageDeclCons = idxFacade.getPackageDeclCon();
		IStrategoList absPathList = termFactory.makeList();
		
		for(String resourceAbsPath : resourceAbsPaths){
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
	 * 
	 * @param fac
	 * @param packageQName
	 * @return {@link IStrategoList}
	 */
	public static IStrategoList getPackageId(SpxSemanticIndexFacade facade,IStrategoAppl packageQName){
		final IStrategoConstructor packageQNameCon = facade.getPackageQNameCon();
		
		if(packageQNameCon == packageQName.getConstructor()){
			return getID( facade, (IStrategoAppl)packageQName.getSubterm(0));	
		}
		
		throw new IllegalArgumentException("Invalid Package Typed QName : "+ packageQName);
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
			newDecl.addFileUri(str); 
		}
		return newDecl;
	}

	public static IStrategoAppl toPackageQNameAppl (SpxSemanticIndexFacade facade, IStrategoList id){
		return toIdTerm(facade ,  facade.getPackageQNameCon(), id);
	}

	
	
	/**
	 * Converts to typed Package Qualified Name
	 *  
	 * @param termFactory
	 * @param decl
	 * @return
	 */
	public static IStrategoAppl toPackageIdTerm (SpxSemanticIndexFacade facade, PackageDeclaration decl){
		return toPackageQNameAppl (facade, decl.getId());
	}

	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.INamespaceFactory#newInstances(org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade)
	 */
	public Iterable<INamespace> newNamespaces(SpxSemanticIndexFacade idxFacade) {
	
		return PackageNamespace.createInstances(id , idxFacade);
	}
}