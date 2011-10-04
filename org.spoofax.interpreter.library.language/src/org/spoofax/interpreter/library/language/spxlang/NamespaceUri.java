package org.spoofax.interpreter.library.language.spxlang;

import java.io.Serializable;
import java.util.UUID;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Md. Adil Akhter
 * 
 */
public final class NamespaceUri implements Serializable
{
	private static final long serialVersionUID = -7525948560476092228L;
	private final IStrategoList _id;
	private final UUID _uId ;

	public NamespaceUri(IStrategoList id){_id = id ; _uId = UUID.randomUUID();}
	
	public IStrategoList id(){ return _id ; }
	
	public String uniqueID(){ return _uId.toString();};
	
	public INamespace resolve(INamespaceResolver sTable) {
		return sTable.resolveNamespace((NamespaceUri)this);
	}
	
	public boolean equalSpoofaxId(IStrategoList spoofaxUri){
		return _id.equals(spoofaxUri);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((_uId == null) ? 0 : _uId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamespaceUri other = (NamespaceUri) obj;
		if (_uId == null) {
			if (other._uId != null)
				return false;
		} else if (!_uId.equals(other._uId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NamespaceId (_id=" + _id + ", _uId=" + _uId + ")";
	}
}
