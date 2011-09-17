package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoTerm;
import static org.spoofax.interpreter.library.language.spxlang.SpxCompilationUnitInfo.toAbsulatePath;
import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;
import jdbm.RecordListener;

/**
 * Record Listener for Compilation Unit.  
 *  
 * @author Md. Adil Akhter
 * Created On : Sep 5, 2011
 */
interface ICompilationUnitRecordListener
{
	public RecordListener<String, SpxCompilationUnitInfo> getCompilationUnitRecordListener();
}



public class SpxCompilationUnitTable {
	
	private PrimaryHashMap<String , SpxCompilationUnitInfo> _infoMap;
	private PrimaryStoreMap<Long,IStrategoTerm> _spxUnitStoreMap;
	
    /**
     * Listeners which are notified about changes in records
     */
    protected List<RecordListener<String,SpxCompilationUnitInfo>> recordListeners = new ArrayList<RecordListener<String,SpxCompilationUnitInfo>>();
    
	/**
	 * Creates a new instance of SymbolTable or loads existing SymbolTable with name specified  
	 * in the following tableName argument.
	 * 
	 * @param tableName name of the SymbolTable
	 * @param manager an instance of IPersistenceManager
	 */
	public SpxCompilationUnitTable(String tableName , ISpxPersistenceManager manager)
	{
		_infoMap = manager.loadHashMap(tableName+ "._infomap.idx");
		_spxUnitStoreMap = manager.loadStoreMap(tableName + "._spxUnitStorageMap.idx");
	}
	
	/**
	 * Adds a {@code listener} in the collection of record listener 
	 * @param listener
	 */
	public void addRecordListener(final RecordListener<String, SpxCompilationUnitInfo> listener) {
		recordListeners.add((RecordListener<String, SpxCompilationUnitInfo>) listener);
	}
	
	public void addRecordListener( final ICompilationUnitRecordListener rl)
	{
		this.addRecordListener(rl.getCompilationUnitRecordListener());
	}
	
	public void removeRecordListener( final ICompilationUnitRecordListener rl)
	{
		this.removeRecordListener(rl.getCompilationUnitRecordListener());
	}
	
	/**
	 * Removes {@code listener} from the collection of record listeners
	 * @param listener
	 */
	public void removeRecordListener(RecordListener<String, SpxCompilationUnitInfo> listener) {	
		recordListeners.remove(listener);
	}
	
	/**
	 * Defines a new symbol table entry. If the entry is already present in the symboltable, 
	 * it updates the existing entry by invoking {@link #update(URI, IStrategoTerm)}. 
	 * 
	 * @param absPath URI representing the absolute path of the Compilation Unit.   
	 * @param compilationUnit compilation unit AST represented by {@link IStrategoTerm}
	 * @throws IOException 
	 */
	public void define(URI absPath , IStrategoTerm compilationUnitRTree) throws IOException
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
	 * @throws IOException 
	 */
	private void add(URI absPath , IStrategoTerm compilationUnitRTree) throws IOException 
	{
		// adding Compilation Unit to the storemap
		long resID = _spxUnitStoreMap.putValue(compilationUnitRTree);
		
		// instantiating a new SpxCompilationUnitInfo object with the newly created resID
		// and storing it in infomap
		SpxCompilationUnitInfo newResInfo = new SpxCompilationUnitInfo(absPath,resID);
		String key = newResInfo.getAbsPathString();
		
		
		_infoMap.put(key, newResInfo);
		
		if(!recordListeners.isEmpty())
		{	
			for(RecordListener<String, SpxCompilationUnitInfo> r:recordListeners)
			{
				r.recordInserted(key, newResInfo);
			}
		}
	}
	
	
	/**
	 * Updates existing symbol table entry. 
	 * 
	 * @param absPath
	 * @param compilationUnitRTree
	 * @throws IOException 
	 */
	private void update(URI absPath , IStrategoTerm compilationUnitRTree) throws IOException
	{	
		
		SpxCompilationUnitInfo oldValue = _infoMap.get(toAbsulatePath(absPath));
		
		SpxCompilationUnitInfo newValue 
			= SpxCompilationUnitInfo.newInstance(oldValue);
		
		_spxUnitStoreMap.put(newValue.getRecId(), compilationUnitRTree);
		newValue.IncrementVersionNo();
	
		if(!recordListeners.isEmpty())
		{	
			for(RecordListener<String, SpxCompilationUnitInfo> r:recordListeners)
			{
				r.recordUpdated(toAbsulatePath(absPath), oldValue , newValue);
			}
		}
	}
	
	
	/**
	 * Removes a SPX Compilation Unit from the symbol table.
	 * 
	 * @param absPath URI for the SPXCompilationUnit to remove
	 * @throws IOException 
	 */
	public void remove(URI absPath) throws IOException
	{
		String key = toAbsulatePath(absPath);
		
		remove(key);
	}
	
	public void verifyUriExists(String uri)	{
		if(!_infoMap.containsKey(uri)){
			throw new IllegalArgumentException(" Unknown CompilationUnit Uri: "+ uri);
		}	
	}
	
	void remove(String absPathString) throws IOException{
		SpxCompilationUnitInfo removedValue = _infoMap.remove(absPathString);
		
		if ( removedValue != null)
			_spxUnitStoreMap.remove(removedValue.getRecId());
		
		if(!recordListeners.isEmpty())
		{	
			for(RecordListener<String, SpxCompilationUnitInfo> r:recordListeners)
			{
				r.recordRemoved(absPathString,removedValue);
			}
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
	
	public void clear() throws IOException{
		Iterator<String> keyIter = _infoMap.keySet().iterator();
		while (keyIter.hasNext())
			remove(keyIter.next());
	}
	
}
