package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.StrategoListIterator;

public abstract class IdentifiableConstruct implements Serializable
{
	private static final long serialVersionUID = 1055862481052307186L;
	private static final String qnameContructorName = "QName";
	private static IStrategoConstructor qnameCon;
	
	final IStrategoList id;
	final Set<IStrategoTerm> imports; 
	
	public IdentifiableConstruct(IStrategoList id) {
		assert id != null;
		
		this.id = id;
		this.imports = new HashSet<IStrategoTerm>();
	}
	
	public IStrategoList getId(){ return id; }

	public abstract IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade);  

	static IStrategoAppl toIdTerm ( SpxSemanticIndexFacade facade , IStrategoConstructor namespaceCon, IStrategoList id)
	{
		ITermFactory factory = facade.getTermFactory();
		
		IStrategoConstructor qnameCons = factory.makeConstructor(qnameContructorName, 1);
		IStrategoAppl qnameAppl = factory.makeAppl(qnameCons, id);
		
		return factory.makeAppl(namespaceCon, qnameAppl);
	}
	
	protected IStrategoTerm forceImploderAttachment(IStrategoTerm term) {
		ImploderAttachment attach = ImploderAttachment.get(id);
		if (attach != null) {
			ImploderAttachment.putImploderAttachment(term, false, attach.getSort(), attach.getLeftToken(), attach.getRightToken());
		} 
		else {
			String fn = getFileLocation();
			term.putAttachment(ImploderAttachment.createCompactPositionAttachment(
					fn, 0, 0, 0, -1));
		}
		return term;
	}
	
	/**
	 * Returns the location of the construct 
	 * 
	 * @return {@link String} representing the absolute path of the  Construct
	 */
	protected String getFileLocation() {
		return null;
	}
	
	/**
	 * Returns {@link IStrategoList} representation of qualified ID of the {@link IdentifiableConstruct}  
	 * 
	 * @param fac an instance of {@link ITermFactory}
	 * @param qName Typed qualified Name of the construct 
	 * 
	 * @return underlying {@link IStrategoList} qualified name
	 */
	protected static IStrategoList getID(SpxSemanticIndexFacade facade, IStrategoAppl qName) {
		
		if ( qnameCon != null)
			qnameCon = facade.getTermFactory().makeConstructor(qnameContructorName, 1);
		
		if(qName.getConstructor() == qnameCon)
			return (IStrategoList)qName.getSubterm(0);
		
		throw new IllegalArgumentException("Invalid QName : " + qName);
	}
	
	
	void appendImports ( IStrategoList  imports) {
		
		for (IStrategoTerm i: StrategoListIterator.iterable(imports)) {
			this.imports.add(i);
		}
	}
	
	public IStrategoList getImports( SpxSemanticIndexFacade idxFacade) 
	{
		ITermFactory termFactory = idxFacade.getTermFactory();
		IStrategoList result = termFactory.makeList();
		
		for (IStrategoTerm t: imports)
			result = termFactory.makeListCons(t, result);
		return result;
	}
	
	/**
	 * Constructs {@link IStrategoList} from {@code decls}  
	 * 
	 * @param idxFacade an instance of {@link SpxSemanticIndexFacade }
	 * @param decls A collection of ModuleDeclataions 
	 * @return {@link IStrategoList}
	 */
	public static <T extends IdentifiableConstruct> IStrategoList toTerm( SpxSemanticIndexFacade idxFacade , Iterable<T> decls)
	{
		ITermFactory termFactory = idxFacade.getTermFactory();
		IStrategoList result = termFactory.makeList();
		
		if(decls!=null)
		{	
			for ( T decl: decls)
				result = termFactory.makeListCons(decl.toTerm(idxFacade), result);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentifiableConstruct other = (IdentifiableConstruct) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.match(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IdentifiableConstruct [id=" + id + "]";
	}
	
}