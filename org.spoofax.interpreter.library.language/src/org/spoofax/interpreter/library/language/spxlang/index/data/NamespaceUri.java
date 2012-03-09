package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.Serializable;
import java.util.UUID;

import org.spoofax.interpreter.library.language.spxlang.index.INamespace;
import org.spoofax.interpreter.library.language.spxlang.index.INamespaceResolver;
import org.spoofax.interpreter.library.language.spxlang.index.SpxIndexUtils;
import org.spoofax.interpreter.terms.IStrategoList;

/**
 * @author Md. Adil Akhter
 * 
 */
@SuppressWarnings("rawtypes")
public class NamespaceUri implements Serializable, Comparable
{
	private static final long serialVersionUID = 7219193145612008432L;
	private final IStrategoList _id;
	private final UUID _uId ;
	private final String _idString;
	
	public NamespaceUri(IStrategoList id, UUID uId){
		_id = id ; 
		_uId = uId;
		
		_idString = SpxIndexUtils.listToString( _id , ".");
	}
	
	public NamespaceUri(IStrategoList id){ 
		this(id, UUID.randomUUID()); 
	}
	
	public IStrategoList id(){ 
		return _id ; 
	}
	
	public String idString(){ 
		return _idString ; 
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
	
	public boolean equalSpoofaxId(String spoofaxUri){
		return _idString.equals(spoofaxUri);
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

	public int compareTo(Object o) {
		return this._idString.compareTo( ((NamespaceUri)o )._idString);
	}
	
	
}
