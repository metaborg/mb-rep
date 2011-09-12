package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

public abstract class BaseConstructDeclaration implements Serializable
{
	private static final long serialVersionUID = 1055862481052307186L;
	protected static final String qnameContructorName = "QName";
	
	final IStrategoList id;
	
	public BaseConstructDeclaration(IStrategoList uri) {
		super();
		this.id = uri;
	}
	
	public IStrategoList getId()
	{
		//returns String representation of the BaseConstruct.
		return id;
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
		BaseConstructDeclaration other = (BaseConstructDeclaration) obj;
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
		return "BaseConstructDeclaration [id=" + id + "]";
	}
	
	public abstract IStrategoTerm toTerm(SpxSemanticIndexFacade idxFacade);  

	public static IStrategoAppl toIdTerm ( ITermFactory factory ,  String constructorName , IStrategoList id)
	{
		IStrategoConstructor cons = factory.makeConstructor(constructorName, 1);
		IStrategoConstructor qnameCons = factory.makeConstructor(qnameContructorName, 1);
		IStrategoAppl qnameAppl = factory.makeAppl(qnameCons, id);
		
		return factory.makeAppl(cons, qnameAppl);
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
	
	protected String getFileLocation() {return null;}
	
	protected static IStrategoList getID(ITermFactory fac, IStrategoAppl qName) {
		
		final IStrategoConstructor qnameCon = fac.makeConstructor(qnameContructorName, 1);
		
		if(qName.getConstructor() == qnameCon)
			return (IStrategoList)qName.getSubterm(0);
		
		throw new IllegalArgumentException("Invalid QName : " + qName);
	}
}