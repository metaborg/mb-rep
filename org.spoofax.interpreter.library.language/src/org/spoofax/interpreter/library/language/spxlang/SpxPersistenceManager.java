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
public class SpxPersistenceManager implements ISpxPersistenceManager {

	//TODO : create a registry that keeps all the loaded SymbolTable
	//and perform operation on that.  
	private final RecordManager _recordManager;
	private final String indexDirectory;  
	
	private SpxCompilationUnitTable _spxUnitsTable;  
	private SpxPackageLookupTable _spxPackageTable;
	private SpxModuleLookupTable _spxModuleTable;

	public SpxPersistenceManager(String projectName, String projectAbsPath) throws IOException{
		this(projectName, projectAbsPath+ "/.Index" , null);
	}
	
	public SpxPersistenceManager (String projectName ,String indexDirectory, Properties options) throws IOException
	{
		this.indexDirectory = indexDirectory;
		
		// Creating empty properties collection if it is null
		if( options  == null)
			options = new Properties();
		
		// setting up the working directory for the Index 
		options.put(RecordManagerOptions.INDEX_RELATIVE_PATH_OPTION, indexDirectory + "/" + projectName + ".idx");
	
		//creating record manager for the particular project
		_recordManager = RecordManagerFactory.createRecordManager(projectName , options);
		
		initTables(projectName);
		initListeners();
	}

	/**
	 * Initializes Symbol Tables for {@code projectName} Project
	 * 
	 * @param projectName name of the Project 
	 */
	private void initTables(String projectName) {
		
		_spxUnitsTable   = new SpxCompilationUnitTable(projectName+"_spxUnitTable", this);
		_spxPackageTable = new SpxPackageLookupTable(projectName+"_spxPackageTable", this);
		_spxModuleTable  = new SpxModuleLookupTable(projectName+"_spxModuleTable", this);
	}
	
	
	/**
	 * Initializes RecordListeners
	 */
	private void initListeners()
	{
		// chain record listeners among the tables. 
		// If compilation unit is removed, automatically remove packages.
		// If package is removed , automatically remove the modules that is located.
		_spxUnitsTable.addRecordListener((ICompilationUnitRecordListener)_spxPackageTable);
		_spxUnitsTable.addRecordListener((ICompilationUnitRecordListener)_spxModuleTable);
	}
	
	
	/**
	 * Instantiates a new HashMap 
	 * 
	 * @param <K>
	 * @param <V>
	 * @param mapName
	 * @return
	 */
	public <K,V> PrimaryHashMap<K,V> loadHashMap ( String mapName)
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
	public <V> PrimaryStoreMap <Long, V> loadStoreMap( String storeMapName)
	{
		return _recordManager.storeMap(storeMapName);
	}
	
	/**
	 * Commits any unsaved changes to the disk 
	 * @throws IOException
	 */
	public void commit() throws IOException
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
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.ISpxPersistenceManager#commitAndClose()
	 */
	public void commitAndClose() throws IOException
	{	
		this.commit();
		this.close();
	}

	public SpxCompilationUnitTable spxCompilcationUnitTable() {
		
		return _spxUnitsTable;
	}

	public boolean IsClosed() {
		
		return _recordManager.IsClosed();
		
	}

	public SpxPackageLookupTable spxPackageTable() {
		return _spxPackageTable;
	}

	public SpxModuleLookupTable spxModuleTable() {
		return _spxModuleTable;
	}
}

