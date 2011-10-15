package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.RecordManagerOptions;

import org.spoofax.interpreter.library.IOAgent;

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
	private static final String SRC =   "SpxPersistenceManager" ;

	private RecordManager _recordManager; 
	private final IOAgent _agent;
	private String _indexId ;

	private SpxCompilationUnitTable _spxUnitsTable; // Keeps a reference to the SpxCompilation Units  
	private SpxPackageLookupTable _spxPackageTable; // Indexing Package and Module Definitions
	private SpxModuleLookupTable _spxModuleTable;
	
	private SpxPrimarySymbolTable _spxSymbolTable;// Symbol Table for storing program symbols 

	/**
	 * Instantiates a new instance of SpxPersistenceManager. Main Responsibility of this class  
	 * is to store symbol table in disk and manage it . 
	 * 
	 * @param spxSemanticIndexFacade
	 * @throws IOException
	 */
	public SpxPersistenceManager(SpxSemanticIndexFacade spxSemanticIndexFacade) throws IOException{
		this(spxSemanticIndexFacade, null);
	}
	
	
	/**
	 * @param projectName
	 * @param indexDirectory
	 * @param ioAgent
	 * @param options
	 * @throws IOException
	 */
	private SpxPersistenceManager (SpxSemanticIndexFacade spxSemanticIndexFacade,Properties options) throws IOException {
		assert spxSemanticIndexFacade != null : "SpxSemanticIndexFacade is expected to be nonnull" ;

		this._agent = spxSemanticIndexFacade.getIOAgent();
		this._indexId = spxSemanticIndexFacade.indexId() ;
		
		String indexDirectory = spxSemanticIndexFacade.getProjectPath()+ "/" + Utils.SPX_INDEX_DIRECTORY;

		if( options  == null)
			options = new Properties();// Creating empty properties collection if it is null
		
		//setting properties of RecordManager
		options.put(RecordManagerOptions.INDEX_RELATIVE_PATH_OPTION, indexDirectory + "/" + _indexId + ".idx");
		options.put(RecordManagerOptions.CACHE_TYPE, "auto");
		options.put(RecordManagerOptions.DISABLE_TRANSACTIONS, "false");
		
		tryInitRecordManager(spxSemanticIndexFacade,options);
		
		logMessage(SRC + ".ctor" , "Instantiation of PersistenceManager is done. Index Directory : ["+ indexDirectory  + "] indexid : "+ getIndexId());
	}


	/**
	 * Tries to initialise record manager
	 * 
	 * @param options
	 * @throws IOException
	 */
	private void tryInitRecordManager(SpxSemanticIndexFacade spxSemanticIndexFacade, Properties options) throws IOException {
		int noOfTries = Utils.NO_OF_ATTEMPT_TO_INIT_RECORDMANAGER;
		
		while(true){
			try {
				_recordManager = RecordManagerFactory.createRecordManager(_indexId , options);
				break;
			}catch(IOException ex) {
				logMessage(SRC + ".tryInitRecordManager" , "Failed to create recordmanager with arg : " + _indexId +". exception : "+ ex);
				if(noOfTries == 0){ 
					logMessage(SRC + ".tryInitRecordManager" , "RecordManager creation is failed. Reason : "+ ex);
					throw ex;
				}else{
					_indexId  = _indexId+ UUID.randomUUID().toString();
					spxSemanticIndexFacade.invalidateSpxCacheDirectory();
				}
			}
		}
	}
	
	/**
	 * Initializes Symbol Tables for {@code projectName} Project
	 * 
	 * @param projectName name of the Project 
	 */
	public void initializeSymbolTables(String projectName , SpxSemanticIndexFacade facade) throws Exception {
		
		_spxUnitsTable   = new SpxCompilationUnitTable(this);
		_spxPackageTable = new SpxPackageLookupTable(this);
		_spxModuleTable  = new SpxModuleLookupTable(this);
		_spxSymbolTable = new SpxPrimarySymbolTable(facade);
		_spxSymbolTable.addGlobalNamespace(facade);

		initListeners();
	}
	
	
	/**
	 * Initializes RecordListeners i.e. chain record listeners among the tables 
	 * to keep the consistency of inter-table symbols.
	 */
	private void initListeners()
	{
		// If compilation unit is removed, automatically remove packages.
		_spxUnitsTable.addRecordListener((ICompilationUnitRecordListener)_spxPackageTable);
		
		// also automatically remove the modules that is located 
		// in that particular compilation units 
		_spxUnitsTable.addRecordListener((ICompilationUnitRecordListener)_spxModuleTable);
		
		
		// whenever a package is deleted from the SpxPackageLookupTable, all the enclosed 
		// modules are also deleted. 
		// Hence, adding following record listener to do that automatically rather invoking
		// it explicitly.
		_spxPackageTable.addRecordListener((IPackageDeclarationRecordListener)_spxModuleTable);
		
		//removing respective package or module namespace associated with the construct declaration  
		_spxPackageTable.addRecordListener((IPackageDeclarationRecordListener)_spxSymbolTable);
		_spxModuleTable.addRecordListener((IModuleDeclarationRecordListener) _spxSymbolTable);
	}
	
	/**
	 * Instantiates a new HashMap 
	 * 
	 * @param <K>
	 * @param <V>
	 * @param mapName
	 * @return
	 */
	public <K,V> PrimaryHashMap<K,V> loadHashMap ( String mapName){
		return _recordManager.hashMap(mapName) ;
		
	}
	
	/**
	 * Instantiates a new StoreHashMap
	 * 
	 * @param <V>
	 * @param storeMapName
	 * @return
	 */
	public <V> PrimaryStoreMap <Long, V> loadStoreMap( String storeMapName) { return _recordManager.storeMap(storeMapName); }
	
	/**
	 * Commits any unsaved changes to the disk 
	 * @throws IOException
	 */
	public void commit() throws IOException {
		this.spxSymbolTable().commit();
		if(!this.isClosed()){
			_recordManager.commit();
		}	
	}
	
	/**
	 * Closes RecordManager
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException { 
		spxSymbolTable().commit();
		
		if(!this.isClosed()){ 
			_recordManager.close();
		}	
		this._spxModuleTable = null;
		this._spxPackageTable = null;
		this._spxUnitsTable = null;
		this._spxSymbolTable = null;
	}
	
	/* (non-Javadoc)
	 * @see org.spoofax.interpreter.library.language.spxlang.ISpxPersistenceManager#commitAndClose()
	 */
	public void commitAndClose() throws IOException { this.commit(); this.close(); }

	public SpxCompilationUnitTable spxCompilcationUnitTable() { return _spxUnitsTable; }

	public boolean isClosed() { return _recordManager == null? true : _recordManager.IsClosed(); }

	public SpxPackageLookupTable spxPackageTable() { return _spxPackageTable; }

	public SpxModuleLookupTable spxModuleTable() { return _spxModuleTable; }	

	public void clear() throws IOException{
		try
		{
			this._spxUnitsTable.clear();	
			this._spxPackageTable.clear();
			this._spxModuleTable.clear();
			this._spxSymbolTable.clear();
			
			logMessage(SRC + ".clearAll", "SymbolTable is cleaned successfully. ");
		}catch(IOException ex)
		{
			logMessage(SRC + ".clearAll", "Exception occured . "+ ex);
			throw ex;
		}
		
		
	}

	/* Logs Message if {@code DEBUG} is turned on.
	 * 
	 * @see org.spoofax.interpreter.library.language.spxlang.ISpxPersistenceManager#logMessage(java.lang.String, java.lang.String)
	 */
	public void logMessage(String origin, String message) {
		if(Utils.DEBUG){		
			try {
				_agent.getWriter(IOAgent.CONST_STDOUT).write(
						"[" + this._indexId + "." + origin + "]   " + message
								+ "\n");
			} 
			catch (IOException e) {}
		}
	}

	public String getIndexId() {
		return _indexId;
	}

	public SpxPrimarySymbolTable spxSymbolTable() {
		return _spxSymbolTable;
	}

	public void rollback() throws IOException{
		_recordManager.rollback();
	}

	public void clearCache() throws IOException {
		_recordManager.clearCache();
	}
}
