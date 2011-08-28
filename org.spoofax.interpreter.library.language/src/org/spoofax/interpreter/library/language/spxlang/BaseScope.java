package org.spoofax.interpreter.library.language.spxlang;


import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * BaseScope  is an abstract base class that implements Scope Interface
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 27, 2011
 */
abstract class BaseScope implements Scope {
	
	private IStrategoTerm _id;
		
	private final ITermFactory _termFactory;
		
	public BaseScope(IStrategoTerm id, ITermFactory termFactory) {
		
		_termFactory = termFactory;
		
		setScopeId(id);
	}
	
	protected ITermFactory getTermFactory() {
		return _termFactory;
	}
	
	public IStrategoTerm getScopeId() {
		return _id;
	}

	private void setScopeId(IStrategoTerm _id) {
		this._id = _id;
	}
	
	public abstract IStrategoTerm getType(); 
	
	
	/* 
	 * Returns the enclosing scope of the current scope.
	 * */
	public Scope getEnclosingScope() {
		return null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
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
		BaseScope other = (BaseScope) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.match(other._id))
			return false;
		return true;
	}

	/* Gets the URI of the current scope. Returns Null is scope is not associated with 
	 * any URI.
	 * 
	 * @see org.spoofax.interpreter.library.language.spxlang.Scope#getScopeURI()
	 */
	public URI getScopeURI() {
		
		return null;
	} 
}
