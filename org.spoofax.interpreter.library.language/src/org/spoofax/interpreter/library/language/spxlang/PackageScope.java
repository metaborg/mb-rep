package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class PackageScope  extends BaseScope {
	
	private final String _scopeType = "Package" ;
	
	public PackageScope(IStrategoTerm id , ITermFactory factory) {
		
		super(id,factory);
		
	}
	
	@Override public IStrategoTerm getType() {
	
		return getTermFactory().makeConstructor(_scopeType, 0);
	}


	@Override public Scope getEnclosingScope() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((_scopeType == null) ? 0 : _scopeType.hashCode());
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
		PackageScope other = (PackageScope) obj;
		if (_scopeType == null) {
			if (other._scopeType != null)
				return false;
		} else if (!_scopeType.equals(other._scopeType))
			return false;
		return true;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PackageScope [_scopeType=" + _scopeType
				+ ", getScopeId()=" + getScopeId() + "]";
	}
	
}