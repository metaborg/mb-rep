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
 * @author Md. Adil Akhter
 * Created On : Aug 22, 2011
 */
class SpxPersistenceManager {

	//TODO : create a registry that keeps all the loaded SymbolTable
	//and perform operation on that.  
	private RecordManager _recordManager = null;
	
	private final String indexDirectory = ".index" ;
	
	public void initialize (String projectName) throws IOException
	{
		initialize( projectName, null);
	}
	
	public void initialize(String projectName , Properties options) throws IOException
	{
		// Creating empty properties collection if it is null
		if( options  == null)
			options = new Properties();
		// setting up the working directory for the Index 
		options.put(RecordManagerOptions.INDEX_RELATIVE_PATH_OPTION, indexDirectory + "/" + projectName);
	
		//If recordmanager is not null, saving and closing the recordmanager
		if ( _recordManager != null)
			commitAndClose();
		
		//creating recordmanager for the particular project
		_recordManager = RecordManagerFactory.createRecordManager(projectName , options);
	}
	
	/* 
	 * Creates named symbol table. If the symbol table already exists , it loads the particular
	 * symbol table.
	 * 
	 * */
	public <K, V>  MultiValuePersistentTable<K, V>  loadTable(String tableName)
	{
		
		return new MultiValuePersistentTable<K, V>( tableName, _recordManager);
	}
		
	/*
	 * Creates a storage table with the name specified. 
	 * */
	public <K, V>  MultiValuePersistentStoreTable<K, V>  loadStorage (String storageName)
	{	
		return new MultiValuePersistentStoreTable<K,V>(storageName,  _recordManager);
	}
	
	private void commit() throws IOException
	{
		_recordManager.commit();
	}
	
	private void close() throws IOException
	{
		_recordManager.close();	
	}
	
	/**
	 * Commits the unsaved and closes the connection. 
	 * @throws IOException
	 */
	public void commitAndClose() throws IOException
	{	
		this.commit();
		this.close();
	}
}

