package org.spoofax.interpreter.library.language.spxlang;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

class NamespaceId 
{
	private final IStrategoList _id;
	private final UUID _uId ;
	
	public NamespaceId(IStrategoList id){_id = id ; _uId = UUID.randomUUID();}
	
	public IStrategoList ID(){ return _id ; }
	
	public String UniqueID(){ return _uId.toString();};
	
	public INamespace resolve(INamespaceResolver sTable) {
		return sTable.resolveNamespace(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
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
		NamespaceId other = (NamespaceId) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.match(other._id))
			return false;
		if (_uId == null) {
			if (other._uId != null)
				return false;
		} else if (!_uId.equals(other._uId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NamespaceId [_id=" + _id + ", _uId=" + _uId + "]";
	}
}


class SpxSymbol{

	private static final long serialVersionUID = -5293805213473800423L;

	private IStrategoList _id;
	private IStrategoTerm _data;
	private IStrategoTerm _type;
	
	private NamespaceId _namespace;
	
	public SpxSymbol (IStrategoList id){_id = id;}
	
	public SpxSymbol (IStrategoList id , IStrategoTerm type){ 
		this(id) ;
		_type = type;
	}
	
	IStrategoList Id(){return _id;}
	
	IStrategoTerm Type() {return _type; }

	IStrategoTerm Data() { return _data; }
	
	NamespaceId Namespace() { return _namespace; }

	void setData(IStrategoTerm data) { _data = data; }

	void setNamespace(NamespaceId id){ _namespace = id;}
	
	boolean equalType (IStrategoTerm term) { return _type == term; }
	
	static List<SpxSymbol> filterByType( IStrategoTerm expectedType , Iterable<SpxSymbol> symbols)
	{
		List<SpxSymbol> retSymbols = new ArrayList<SpxSymbol>();
		
		for(SpxSymbol s : symbols){
			if( s.equalType(expectedType) ){ retSymbols.add(s) ;}
		}
		return retSymbols;
	}
}