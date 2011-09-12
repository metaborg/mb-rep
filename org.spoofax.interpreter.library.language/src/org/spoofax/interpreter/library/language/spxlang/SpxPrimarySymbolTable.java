package org.spoofax.interpreter.library.language.spxlang;

import jdbm.PrimaryMap;

import org.spoofax.interpreter.terms.IStrategoTerm;

public class SpxPrimarySymbolTable {
	
	private final ISpxPersistenceManager _manager;
	private final PrimaryMap <ScopeIdentifier , IScope> _scopeLookup;
	
	public SpxPrimarySymbolTable (String tableName, ISpxPersistenceManager manager)
	{
		_manager = manager;
		
		_scopeLookup  = _manager.loadHashMap(tableName + "_scopeLookup.idx");
		
	}
	
	public ScopeIdentifier getScopeID(IStrategoTerm spxSymbolId)
	{
		return null;
	}
	
	public IScope getScope( ScopeIdentifier scopeId)
	{
		// lookup ScopeTree for the scopeId and return it.
		return null;
	}
}
