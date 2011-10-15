package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.Serializable;
import java.util.UUID;

import org.spoofax.interpreter.library.language.spxlang.index.INamespace;
import org.spoofax.interpreter.library.language.spxlang.index.INamespaceResolver;
import org.spoofax.interpreter.terms.IStrategoList;

/**
 * @author Md. Adil Akhter
 * 
 */
public class NamespaceUri implements Serializable
{
	private static final long serialVersionUID = 7219193145612008432L;
	private final IStrategoList _id;
	private final UUID _uId ;

	public NamespaceUri(IStrategoList id, UUID uId){_id = id ; _uId = uId; }
	
	public NamespaceUri(IStrategoList id){ 
		this(id, UUID.randomUUID()); 
	}
	
	public IStrategoList id(){ 
		if( _id == null) {
			
		}
		return _id ; 
	}
	
	public String uniqueID(){ return _uId.toString();};
	
	public INamespace resolve(INamespaceResolver sTable) throws SpxSymbolTableException {
		INamespace retNamespace = sTable.resolveNamespace((NamespaceUri)this);
		
		if(retNamespace == null) {
			throw new SpxSymbolTableException("Unknown Namespace Uri. Namespace can not be resolved from symbol-table") ;
		}
		return retNamespace;
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
		return "NamespaceId [ID =" + _id + "]";
	}
}
