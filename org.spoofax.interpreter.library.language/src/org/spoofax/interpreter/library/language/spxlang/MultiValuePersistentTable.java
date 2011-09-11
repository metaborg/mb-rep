package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.util.ArrayList;

import jdbm.PrimaryMap;
import jdbm.RecordManager;

/**
 * Generic MultiValue Symbol Table to store the symbols. 
 * Symbols are stored in memory and persisted
 * on the disk if committed via {@link SpxPersistenceManager}.
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 22, 2011
 */
class MultiValuePersistentTable<K, V> {

	private final PrimaryMap<K,ArrayList<V>> _primaryMap;
	private final RecordManager _recordManager;
	private String _mapName ;
	
	public MultiValuePersistentTable(String name, RecordManager manager)
	{
		_mapName  = name ;
		
		_recordManager = manager;
	
		_primaryMap = _recordManager.hashMap(_mapName);
	}
	
	/**
	 * removes all the entries from current map
	 * 
	 * @throws IOException 
	 */
	public void clear() throws IOException
	{
		_primaryMap.clear();
		
		_recordManager.commit();
	}
	
	/**
	 * Defines symbol in the current symbol table. Define does not replace  
	 * old symbol mapped using the key with the new one. It just adds the 
	 * new symbol at the end of the multivalue-list. 
	 * 
	 * @param key - The key that the symbol will be mapped to .
	 * @param symbol - The symbol to store. 
	 */
	public void define(K key , V symbol)
	{	
		if ( _primaryMap.containsKey(key))
			_primaryMap.get(key).add(symbol);
		else
		{
			ArrayList<V> values = new ArrayList<V>(); 
			values.add(symbol);
			
			_primaryMap.put( key , values );
		}
	}
}
