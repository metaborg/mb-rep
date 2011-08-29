package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoTerm;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;

class SpxCompilationUnitSymbolTable {
	
	PrimaryHashMap<String , SpxComplicationUnitInfo> _infoMap;
	
	PrimaryStoreMap<Long,IStrategoTerm> _unitStorage;
	
	
	public SpxCompilationUnitSymbolTable( SpxPersistenceManager manager)
	{
		
		
	}
	
}
