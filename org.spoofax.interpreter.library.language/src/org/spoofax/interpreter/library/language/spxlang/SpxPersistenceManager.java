package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.util.Properties;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.RecordManagerOptions;

/**
 * PersistenceManager responsible for initializing and maintaining various 
 * symbol table needed for SpoofaxLang Implementation. 
 * 
 * It hosts all the SymbolTables. SymbolTables are loaded whenever is needed 
 * and changes are persisted when following operation is performed : {@link SpxPersistenceManager}.commit.
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 22, 2011
 */
class SpxPersistenceManager {

	//TODO : create a registry that keeps all the loaded SymbolTable
	//and perform operation on that.  
	private final RecordManager _recordManager;
	
	private final String indexDirectory = ".index" ;
	
	//TODO : All the maps will be hosted by this  object 
	public SpxPersistenceManager(String projectName) throws IOException
	{
		this( projectName, null);
	}
	
	public SpxPersistenceManager (String projectName , Properties options) throws IOException
	{
		// Creating empty properties collection if it is null
		if( options  == null)
			options = new Properties();
		
		// setting up the working directory for the Index 
		options.put(RecordManagerOptions.INDEX_RELATIVE_PATH_OPTION, indexDirectory + "/" + projectName);
	
		//creating recordmanager for the particular project
		_recordManager = RecordManagerFactory.createRecordManager(projectName , options);
	}
	
	
	/**
	 * Instantiates a new HashMap 
	 * 
	 * @param <K>
	 * @param <V>
	 * @param mapName
	 * @return
	 */
	<K,V> PrimaryHashMap<K,V> loadHashMap ( String mapName)
	{
		return _recordManager.hashMap(mapName) ;
		
	}
	
	
	/**
	 * Instantiates a new StoreHashMap
	 * 
	 * @param <V>
	 * @param storeMapName
	 * @return
	 */
	<V> PrimaryStoreMap <Long, V> loadStoreMap( String storeMapName)
	{
		return _recordManager.storeMap(storeMapName);
	}
	
	
	/**
	 * Commits any unsaved changes to the disk 
	 * @throws IOException
	 */
	void commit() throws IOException
	{
		_recordManager.commit();
	}
	
	
	/**
	 * Closes RecordManager
	 * 
	 * @throws IOException
	 */
	void close() throws IOException
	{
		_recordManager.close();	
	}
	
	/**
	 * Commits the unsaved and closes the connection.
	 *  
	 * @throws IOException
	 */
	public void commitAndClose() throws IOException
	{	
		this.commit();
		this.close();
	}
}

