package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Md. Adil Akhter
 * Created On : Aug 21, 2011
 */

class ModuleScope extends BaseScope
{
	private Scope _enclosingScope;
	
	private final String _scopeType = "Module" ;
	
	private URI _file; 
	/**
	 * Instantiates ModuleScope.  
	 * 
	 * @param id refers to the ID of the Scope
	 * @param enclosingScope refers to the enclosing scope. It
	 * provides a reference to the container of the current Scope.  
	 */
	public ModuleScope(IStrategoTerm id, Scope enclosingScope , ITermFactory factory , URI file) {
		super(id,factory);

		this._enclosingScope = enclosingScope;
		this._file = file;
	}
	
	public Scope getEnclosingScope() {
		return _enclosingScope;
	}
	
	/**
	 * Returns the type of the Scope 
	 */
	public IStrategoTerm getType() {
		return getTermFactory().makeConstructor(_scopeType, 0);
	}

	/* 
	 * (non-Javadoc)
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
		
		ModuleScope other = (ModuleScope) obj;
		if (_enclosingScope == null) {
			if (other._enclosingScope != null)
				return false;
		} else if (!_enclosingScope.equals(other._enclosingScope))
			return false;
		if (_file == null) {
			if (other._file != null)
				return false;
		} else if (!_file.equals(other._file))
			return false;
		if (_scopeType == null) {
			if (other._scopeType != null)
				return false;
		} else if (!_scopeType.equals(other._scopeType))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((_enclosingScope == null) ? 0 : _enclosingScope.hashCode());
		result = prime * result + ((_file == null) ? 0 : _file.hashCode());
		result = prime * result
				+ ((_scopeType == null) ? 0 : _scopeType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ModuleScope [" +
				"getScopeId()=" + getScopeId() + "]"		
				+",enclosingScope=" + _enclosingScope
				+ ", _scopeType=" + _scopeType + ", _file=" + _file ;
	}
}
