package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import static org.spoofax.interpreter.library.language.spxlang.SpxCompilationUnitInfo.toAbsulatePath;

import org.spoofax.interpreter.terms.IStrategoTerm;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;

class SpxCompilationUnitSymbolTable {
	
	PrimaryHashMap<String , SpxCompilationUnitInfo> _infoMap;
	
	PrimaryStoreMap<Long,IStrategoTerm> _spxUnitStoreMap;
	
	
	/**
	 * Creates a new instance of SymbolTable or loads existing SymbolTable with name specified  
	 * in the following tableName argument.
	 * 
	 * @param tableName name of the SymbolTable
	 * @param manager an instance of IPersistenceManager
	 */
	public SpxCompilationUnitSymbolTable(String tableName , ISpxPersistenceManager manager)
	{
		_infoMap = manager.loadHashMap(tableName+ "._infomap.idx");
		_spxUnitStoreMap = manager.loadStoreMap(tableName + "._spxUnitStorageMap.idx");
	}
	
	/**
	 * Defines a new symbol table entry. If the entry is already present in the symboltable, 
	 * it updates the existing entry by invoking {@link #update(URI, IStrategoTerm)}. 
	 * 
	 * @param absPath URI representing the absolute path of the Compilation Unit.   
	 * @param compilationUnit compilation unit AST represented by {@link IStrategoTerm}
	 */
	public void define(URI absPath , IStrategoTerm compilationUnitRTree)
	{	
		String abspathString = toAbsulatePath(absPath);
		
		if ( _infoMap.containsKey(abspathString))
			this.update(absPath, compilationUnitRTree); //URI is already there in the symbol table . Hence updating the table
		else
			this.add(absPath, compilationUnitRTree);  
	}
	
	/**
	 * Adds the new CompilationUnit.
	 * 
	 * @param absPath
	 * @param compilationUnitRTree
	 */
	private void add(URI absPath , IStrategoTerm compilationUnitRTree) 
	{
		// adding Compilation Unit to the storemap
		long resID = _spxUnitStoreMap.putValue(compilationUnitRTree);
		
		// instantiating a new SpxCompilationUnitInfo object with the newly created resID
		// and storing it in infomap
		SpxCompilationUnitInfo newResInfo = new SpxCompilationUnitInfo(absPath,resID);
		_infoMap.put(newResInfo.getAbsPathString(), newResInfo);
	}
	
	
	/**
	 * Updates existing symbol table entry. 
	 * 
	 * @param absPath
	 * @param compilationUnitRTree
	 */
	private void update(URI absPath , IStrategoTerm compilationUnitRTree)
	{	
		
		// key is already there in the info map .
		SpxCompilationUnitInfo resInfoToUpdate = _infoMap.get( toAbsulatePath(absPath));
		
		_spxUnitStoreMap.put(resInfoToUpdate.getRecId(), compilationUnitRTree);
		
		resInfoToUpdate.IncrementVersionNo();
	}
	
	
	/**
	 * Removes a SPX Compilation Unit from the symbol table.
	 * 
	 * @param absPath URI for the SPXCompilationUnit to remove
	 */
	public void remove(URI absPath)
	{
		String key = toAbsulatePath(absPath);
		
		SpxCompilationUnitInfo resInfoToUpdate = _infoMap.remove(key);
		
		if ( resInfoToUpdate != null)
		{	
			_spxUnitStoreMap.remove(resInfoToUpdate.getRecId());
		}
	}
	
	/**
	 * Returns SPXCompilationUnit mapped by the specified absPath argument.
	 * 
	 * @param absPath
	 * @return
	 */
	public IStrategoTerm get(URI absPath)
	{
		String key = toAbsulatePath(absPath);
		
		SpxCompilationUnitInfo retUnitData= _infoMap.get(key);
		
		return _spxUnitStoreMap.get(retUnitData.getRecId());
		
	}
}
