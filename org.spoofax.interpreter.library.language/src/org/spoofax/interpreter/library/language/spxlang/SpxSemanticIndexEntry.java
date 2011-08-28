package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 * Created On : Aug 20, 2011
 */
class SpxSemanticIndexEntry {

	
	private Scope _scope; 
	
	private IStrategoTerm data;
	
	public SpxSemanticIndexEntry (Scope scope)
	{
		setScope(scope);
	}
	
	
	public Scope getScope() {
		return _scope;
	}

	private void setScope(Scope scope) {
		this._scope = scope;
	}

	/**
	 * @return the data
	 */
	protected IStrategoTerm getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	protected void setData(IStrategoTerm data) {
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IndexEntry [_scope=" + _scope + ", data=" + data
				+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_scope == null) ? 0 : _scope.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		SpxSemanticIndexEntry other = (SpxSemanticIndexEntry) obj;
		if (_scope == null) {
			if (other._scope != null)
				return false;
		} else if (!_scope.equals(other._scope))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.match(other.data))
			return false;
		return true;
	}
}



class SpxSemanticIndexKey extends SpxSemanticIndexEntry
{	
	public SpxSemanticIndexKey(Scope scope, IStrategoTerm key)
	{
		super(scope);
	}
	
	public IStrategoTerm getKey()
	{
		return getData(); 
		
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxSemanticIndexKey [ " +
				"key =" + getData().toString() +
				"scope="  + getScope().toString() +
				"]";
	}
}


class SpxSemanticIndexSymbol extends SpxSemanticIndexEntry
{
	private final IStrategoTerm _key ;

	public SpxSemanticIndexSymbol ( Scope scope, IStrategoTerm symbol, IStrategoTerm key)
	{
		super(scope);
		
		_key = key;
		setData(symbol);
	}
	
	
	/**
	 * @return the _key
	 */
	public IStrategoTerm getKey() {
		return _key;
	}

	
	public IStrategoTerm getValue()
	{
		return getData();
	}


	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((_key == null) ? 0 : _key.hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		SpxSemanticIndexSymbol other = (SpxSemanticIndexSymbol) obj;
		if (_key == null) {
			if (other._key != null)
				return false;
		} else if (!_key.match(other._key))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpxSemanticIndexSymbol [ " +
				"key=" + _key.toString() + 
				"symbol=" + getData().toString() +
				"scope="  + getScope().toString() +
				"]";
	}
	
}


