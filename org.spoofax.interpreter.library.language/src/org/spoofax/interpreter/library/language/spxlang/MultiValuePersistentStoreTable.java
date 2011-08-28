package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jdbm.PrimaryMap;
import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;

//TODO : convert it to a actual HashMap . Currently, it is just acting 
// as a wrapper around HashMap
class MultiValuePersistentStoreTable<K, V> {

	private final PrimaryMap<K, ArrayList<Long>> _keyMap;
	
	private final PrimaryStoreMap<Long, V> _valueMap;

	private final RecordManager _recordManager;

	/**
	 * Creates a multivalue hash table for storing/indexing symbols.
	 * 
	 * @param name The name of the symbol table .
	 * @param manager RecordManager to manage physical values. 
	 */
	public MultiValuePersistentStoreTable(String name, RecordManager manager)
	{
		_recordManager = manager;
		
		_keyMap = _recordManager.hashMap("__"+name+"_Key");

		_valueMap = _recordManager.storeMap(name);
	}

	
	/**
	 * Clears the storage.
	 * @throws IOException 
	 */
	public void clear() throws IOException {
		synchronized(this)
		{
			//Clears the symbol table
			_valueMap.clear();
			_keyMap.clear();
			
			
			// Committing the changes of symbol table.
			_recordManager.commit();
		}
		
	}

	
	/**
	 * Adds the value in the symbol table
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public void define( K key , V value)
	{
		synchronized(this)
		{	
			long recid = _valueMap.putValue(value); 
			
			if ( _keyMap.containsKey(key))
			{
				_keyMap.get(key).add(recid);
			}
			else
			{
				ArrayList<Long> recIdsMappedUsingKey  = new ArrayList<Long>();
				recIdsMappedUsingKey.add(recid);
			
				_keyMap.put(key, recIdsMappedUsingKey);
			}	
		}
	}
	
	
	/**
	 * Returns the list of Keys
	 * 
	 * @return keyset
	 */
	public Set<K> keySet() {
		return _keyMap.keySet();
	}
	
	
	/**
	 * Gets the collection of values that are hashed using the key specified.
	 * @param key  The Key whose associated value is to be returned
	 * @return The values to which this key is mapped to in this Symbol Table. 
	 * Returns empty list if no mapping for the keys is not found.   
	 */
	public List<V> resolve(K key) 
	{
		ArrayList<V> storageElements = new ArrayList<V>();
		
		if(_keyMap.containsKey(key))
		{
			ArrayList<Long> physicalKeys = getPhysicalStorageKeys(key) ;
	
			if (physicalKeys!=null)
			{
				for( Long l : physicalKeys)
				{
					addToCollection ( l , storageElements);
				}
			}
		}
		return storageElements;
	}
	

	/**
	 * Adds value hashed using physical keys in the collection
	 * 
	 * @param physicalKey
	 * @param collectionToAdd
	 */
	private void addToCollection( long physicalKey, List<V> collectionToAdd)
	{
		if (_valueMap.containsKey(physicalKey))
		{
			collectionToAdd.add(_valueMap.get(physicalKey));
		}
	}
	
	/**
	 * Returns the physical storage keys. Given virtual key, it gets the physical keys for the storage elements.
	 * @param key virtual key 
	 * @return  Collection of Physical Storage Key
	 */
	private ArrayList<Long> getPhysicalStorageKeys(K key) {
		return _keyMap.get(key);
	}


	/**
	 * Verifies whether key exists in the current symbol table.
	 * 
	 * @param key The key whose presence to be tested 
	 * 
	 * @return true if the table contains a mapping for the specified key. 
	 */
	public boolean containsKey(Object key) {
		return _keyMap.containsKey(key);
	}

}
