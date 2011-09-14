package org.spoofax.interpreter.library.language.spxlang;

import java.util.HashSet;
import java.util.Set;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoListIterator;

/**
 * Defines several properties of the Language specified in the 
 * package scope. 
 * 
 * @author Md. Adil Akhter
 * Created On : Sep 3, 2011
 */
public class LanguageDescriptor extends IdentifiableConstruct {

	private static final long serialVersionUID = 7099736990601308645L;
	
	static final int LanguageNamesIndex = 1;
	static final int LanguageIdsIndex = 2;
	static final int EsvStartSymbolsIndex = 3;
	static final int SdfStartSymbolsIndex = 4;
	
	private IStrategoList languageNames;
	private IStrategoList languageIDs;
	private IStrategoList esvDeclaredStartSymbols;
	private IStrategoList sdfDeclaredStartSymbols;
	
	public LanguageDescriptor(IStrategoList id) {
		super(id);
	}
	/**
	 * @return the languageNames
	 */
	public IStrategoList getLanguageNames() {
		return languageNames;
	}
	/**
	 * @return the languageIDs
	 */
	public IStrategoList getLanguageIDs() {
		return languageIDs;
	}
	/**
	 * @return the esvDeclaredStartSymbols
	 */
	public IStrategoList getEsvDeclaredStartSymbols() {
		return esvDeclaredStartSymbols;
	}
	/**
	 * @return the sdfDeclaredStartSymbols
	 */
	public IStrategoList getSdfDeclaredStartSymbols() {
		return sdfDeclaredStartSymbols;
	}

	public void addLanguageNames( ITermFactory fac , IStrategoList langNames) 
	{ 
		if (languageNames == null)
			this.languageNames = fac.makeList();
	
		this.languageNames =  prepend(fac, languageNames , langNames);
	}

	public void addLanguageIDs( ITermFactory fac , IStrategoList langIds) 
	{ 
		languageIDs =  prepend(fac, languageIDs , langIds);
	}
	
	public void addEsvDeclaredStartSymbols( ITermFactory fac , IStrategoList startSymbols)
	{
		this.esvDeclaredStartSymbols =  prepend(fac, this.esvDeclaredStartSymbols  , startSymbols);
	}
	
	public void addSDFDeclaredStartSymbols( ITermFactory fac , IStrategoList startSymbols)
	{
		this.esvDeclaredStartSymbols =  prepend(fac, this.esvDeclaredStartSymbols  , startSymbols);
	}
	
	public Iterable<String> asLanguageNameStrings()
	{
		Set<String> langNames = new HashSet<String>();
		for (IStrategoTerm lName: StrategoListIterator.iterable(this.languageNames)) {
			langNames.add(Tools.asJavaString(lName));
		}
		return langNames;
	}
	
	/**
	 * Creates a new instance of {@link LanguageDescriptor}
	 * @param fac
	 * @param decl
	 * @return
	 */
	public static LanguageDescriptor newInstance(ITermFactory fac, LanguageDescriptor decl)
	{
		return newInstance(fac, 
				decl.getId(), 
				decl.languageIDs, 
				decl.languageNames, 
				decl.sdfDeclaredStartSymbols, 
				decl.esvDeclaredStartSymbols);
		
	}

	public static LanguageDescriptor newInstance( ITermFactory f,  IStrategoList packageId , IStrategoList languageIds, IStrategoList languageNames,IStrategoList sdfStartSymbols,IStrategoList esvStartSymbols)
	{
		LanguageDescriptor desc = new LanguageDescriptor(packageId);

		desc.addLanguageIDs(f, languageIds);
		desc.addLanguageNames( f, languageNames);
		desc.addSDFDeclaredStartSymbols(f, sdfStartSymbols);
		desc.addEsvDeclaredStartSymbols(f, esvStartSymbols);

		return desc;
	}
	
	private static IStrategoList prepend(ITermFactory fac, IStrategoList srcList, IStrategoList toPrepend) {
		if ( toPrepend == null)
			return srcList;
		
		if ( srcList == null)
			srcList = fac.makeList();

		for (IStrategoTerm result: StrategoListIterator.iterable(toPrepend)) {
			srcList = fac.makeListCons(result, srcList);
		}
		return srcList;
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
				+ ((esvDeclaredStartSymbols == null) ? 0
						: esvDeclaredStartSymbols.hashCode());
		result = prime * result
				+ ((languageIDs == null) ? 0 : languageIDs.hashCode());
		result = prime * result
				+ ((languageNames == null) ? 0 : languageNames.hashCode());
		result = prime
				* result
				+ ((sdfDeclaredStartSymbols == null) ? 0
						: sdfDeclaredStartSymbols.hashCode());
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
		if (!(obj instanceof LanguageDescriptor))
			return false;
		LanguageDescriptor other = (LanguageDescriptor) obj;
		
		if (esvDeclaredStartSymbols == null) {
			if (other.esvDeclaredStartSymbols != null)
				return false;
		} else if (!esvDeclaredStartSymbols.match(other.esvDeclaredStartSymbols))
			return false;
		
		if (languageIDs == null) {
			if (other.languageIDs != null)
				return false;
		} else if (!languageIDs.match(other.languageIDs))
			return false;
		if (languageNames == null) {
			if (other.languageNames != null)
				return false;
		} else if (!languageNames.match(other.languageNames))
			return false;
		if (sdfDeclaredStartSymbols == null) {
			if (other.sdfDeclaredStartSymbols != null)
				return false;
		} else if (!sdfDeclaredStartSymbols.match(other.sdfDeclaredStartSymbols))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LanguageDescriptor [languageNames=" + languageNames
				+ ", languageIDs=" + languageIDs + ", esvDeclaredStartSymbols="
				+ esvDeclaredStartSymbols + ", sdfDeclaredStartSymbols="
				+ sdfDeclaredStartSymbols + "]";
	}

	@Override
	public IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade) {
		
		ITermFactory termFactory = idxFacade.getTermFactory();
		
		IStrategoConstructor ctr = idxFacade.getLanguageDescriptorCon();
		IStrategoAppl packageQNameAppl = PackageDeclaration.toPackageIdTerm(termFactory, this.getId());
		
		IStrategoTerm retTerm = termFactory.makeAppl(ctr, packageQNameAppl , this.languageNames , this.languageIDs , this.esvDeclaredStartSymbols , this.sdfDeclaredStartSymbols);
		
		return this.forceImploderAttachment(retTerm);
	} 
}
