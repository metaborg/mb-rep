package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;

class SpxCompilationUnitSymbolTable {
	
	PrimaryHashMap<String , SpxCompilationUnitInfo> _infoMap;
	
	PrimaryStoreMap<Long,IStrategoTerm> _spxUnitStorageMap;
	
	
	/**
	 * 
	 * @param tableName
	 * @param manager
	 */
	public SpxCompilationUnitSymbolTable(String tableName , ISpxPersistenceManager manager)
	{
		_infoMap = manager.loadHashMap(tableName+ "._infomap");
		_spxUnitStorageMap = manager.loadStoreMap(tableName + "._spxUnitStorageMap");
	}
	
	/**
	 * @param info
	 * @param compilationUnit
	 */
	public void define ( SpxCompilationUnitInfo info, IStrategoAppl compilationUnit)
	{
		
	}
	
}
