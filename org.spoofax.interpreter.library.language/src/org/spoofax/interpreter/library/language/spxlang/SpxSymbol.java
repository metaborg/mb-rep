package org.spoofax.interpreter.library.language.spxlang;

import java.util.UUID;

import org.spoofax.interpreter.terms.IStrategoTerm;

class ScopeIdentifier 
{
	private UUID _scopeId;
	
	public ScopeIdentifier( UUID scopeId)
	{
		_scopeId = scopeId;
	}
	
	public IScope resolve( ISpxPersistenceManager manager)
	{
		return null;
	}
}


/**
 * @author Md. Adil Akhter
 * Created On : Aug 20, 2011
 */
class SpxSymbol {
	
	private String _name;
	private IStrategoTerm _data;
	private ISpxType _type;
	private ScopeIdentifier _scope;
	
	
	public SpxSymbol (String name){	_name = name; }
	public SpxSymbol (String name , ISpxType type){ this(name) ; _type = type;}
	
	public String getName() { return _name; }
	
	/**
	 * @return the data
	 */
	protected IStrategoTerm getData() {
		return _data;
	}

	/**
	 * @param data the data to set
	 */
	protected void setData(IStrategoTerm data) {
		this._data = data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxSymbol[_scope =" + _scope + ", data=" + _data + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_scope == null) ? 0 : _scope.hashCode());
		result = prime * result + ((_data == null) ? 0 : _data.hashCode());
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
		SpxSymbol other = (SpxSymbol) obj;
		if (_scope == null) {
			if (other._scope != null)
				return false;
		} else if (!_scope.equals(other._scope))
			return false;
		if (_data == null) {
			if (other._data != null)
				return false;
		} else if (!_data.match(other._data))
			return false;
		return true;
	}
}



//
///**
// * @author Md. Adil Akhter
// * Created On : Sep 1, 2011
// */
//class SpxSemanticIndexKey extends SpxSymbol
//{	
//	public SpxSemanticIndexKey(Scope scope, IStrategoTerm key)
//	{
//		super(scope);
//	}
//	
//	public IStrategoTerm getKey()
//	{
//		return getData(); 
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		return "SpxSemanticIndexKey [ " +
//				"key =" + getData().toString() +
//				"scope="  + getScope().toString() +
//				"]";
//	}
//}
//
///**
// * 
// * @author Md. Adil Akhter
// * Created On : Sep 1, 2011
// */
//class SpxSemanticIndexSymbol extends SpxSymbol
//{
//	private final IStrategoTerm _key ;
//
//	public SpxSemanticIndexSymbol ( Scope scope, IStrategoTerm symbol, IStrategoTerm key)
//	{
//		super(scope);
//		
//		_key = key;
//		setData(symbol);
//	}
//	
//	/**
//	 * 
//	 * @return the _key
//	 */
//	public IStrategoTerm getKey() {
//		return _key;
//	}
//
//	public IStrategoTerm getValue()
//	{
//		return getData();
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#hashCode()
//	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((_key == null) ? 0 : _key.hashCode());
//		return result;
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		SpxSemanticIndexSymbol other = (SpxSemanticIndexSymbol) obj;
//		if (_key == null) {
//			if (other._key != null)
//				return false;
//		} else if (!_key.match(other._key))
//			return false;
//		return true;
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		return "SpxSemanticIndexSymbol [ " +
//				"key=" + _key.toString() + 
//				"symbol=" + getData().toString() +
//				"scope="  + getScope().toString() +
//				"]";
//	}
//}

