package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class GlobalScope extends BaseScope {
	
	private final String _scopeType = "Global" ;
	
	public GlobalScope(IStrategoTerm id , ITermFactory factory) {
		
		super(id , factory);
	}
	
	/**
	 * Returns the type of the current Scope 
	 */
	public IStrategoTerm getType() {
		
		return getTermFactory().makeConstructor(_scopeType, 0);
	}
	
	
}
