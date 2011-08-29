package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;

class SpxCompilationUnitSymbolTable {
	
	PrimaryHashMap<String , SpxCompilationUnitInfo> _infoMap;
	
	PrimaryStoreMap<Long,IStrategoTerm> _unitStorage;
	
	
	public SpxCompilationUnitSymbolTable(SpxPersistenceManager manager)
	{
		
	}
	
	
	public void define ( SpxCompilationUnitInfo info, IStrategoAppl compilationUnit)
	{
		
	}
	
}
