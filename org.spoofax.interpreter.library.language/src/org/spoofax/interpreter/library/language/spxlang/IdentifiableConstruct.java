package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
	
	protected final IStrategoList id;
	protected final Set<IStrategoTerm> importReferences; 
	
	public IdentifiableConstruct(IStrategoList id) {
		assert id != null :  " ID can not be null " ;

		this.id = id;
		this.importReferences = new HashSet<IStrategoTerm>();
	}
	
	public IStrategoList getId(){ return id; }  
	
	public abstract IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade);
	
	public void addImportRefernces ( IStrategoList  imports) {
	
		for (IStrategoTerm i: StrategoListIterator.iterable(imports)) {
			this.importReferences.add(i);
		}
	}
	
	protected Set<IStrategoTerm> getEnclosedImportReferences(SpxSemanticIndexFacade idxFacade) { return new HashSet<IStrategoTerm>(); } 
	
	public Set<IStrategoTerm> getImportReferneces() {return importReferences ; }
	
	IStrategoList getImports(SpxSemanticIndexFacade idxFacade) {	
		ITermFactory termFactory = idxFacade.getTermFactory();
		
		HashSet<IStrategoTerm> allImportRefs = new HashSet<IStrategoTerm>();
		allImportRefs.addAll(this.importReferences);
		allImportRefs.addAll(getEnclosedImportReferences(idxFacade));
		
		IStrategoList result = termFactory.makeList();
		for (IStrategoTerm t: allImportRefs)
			result = idxFacade.getTermFactory().makeListCons(t, result);
	
		return result;
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
	protected String getFileLocation() { return null; }
	
	/**
	 * Constructs {@link IStrategoList} from {@code decls}  
	 * 
	 * @param idxFacade an instance of {@link SpxSemanticIndexFacade }
	 * @param decls A collection of ModuleDeclataions 
	 * @return {@link IStrategoList}
	 */
	public static <T extends IdentifiableConstruct> IStrategoList toTerm( SpxSemanticIndexFacade idxFacade , Iterable<T> decls){
		ITermFactory termFactory = idxFacade.getTermFactory();
		IStrategoList result = termFactory.makeList();
		
		if(decls!=null){	
			for ( T decl: decls)
				result = termFactory.makeListCons(decl.toTerm(idxFacade), result);
		}
		
		return result;
	}

	static IStrategoAppl toIdTerm ( SpxSemanticIndexFacade facade , IStrategoConstructor namespaceCon, IStrategoList id)
	{
		ITermFactory factory = facade.getTermFactory();
		
		IStrategoConstructor qnameCons = facade.getQNameCon();
		IStrategoAppl qnameAppl = factory.makeAppl(qnameCons, id);
		return factory.makeAppl(namespaceCon, qnameAppl);
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
		
		if(qName.getConstructor() == facade.getQNameCon())
			return (IStrategoList)qName.getSubterm(0);
		
		throw new IllegalArgumentException("Invalid QName : " + qName);
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IdentifiableConstruct [id=" + id + "]";
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

}