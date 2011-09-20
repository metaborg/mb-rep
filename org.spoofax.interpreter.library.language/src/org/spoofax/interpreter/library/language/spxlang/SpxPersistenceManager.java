package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.util.Properties;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoTerm;

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

	private static final boolean DEBUG = true;
	private static final String SRC =   "SpxPersistenceManager" ;
	
	
	private final RecordManager _recordManager;
	private final String _indexDirectory;  
	private final IOAgent _agent;
	private final String _projectName ;
	
	private SpxCompilationUnitTable _spxUnitsTable;  
	private SpxPackageLookupTable _spxPackageTable;
	private SpxModuleLookupTable _spxModuleTable;

	/**
	 * Instantiates a new instance of SpxPersistenceManager. Main Responsibility of this class  
	 * is to store symbol table in disk and manage it . 
	 * 
	 * @param projectName
	 * @param projectAbsPath
	 * @param ioAgent
	 * @throws IOException
	 */
	public SpxPersistenceManager(String projectName, String projectAbsPath , IOAgent ioAgent) throws IOException{
		this(projectName, projectAbsPath+ "/.Index" ,ioAgent, null );
	}
	
	
	/**
	 * @param projectName
	 * @param indexDirectory
	 * @param ioAgent
	 * @param options
	 * @throws IOException
	 */
	SpxPersistenceManager (String projectName ,String indexDirectory,  IOAgent ioAgent,Properties options) throws IOException
	{
		this._agent = ioAgent;
		this._projectName = projectName;
		this._indexDirectory = indexDirectory;
		
		if( options  == null)
			options = new Properties();// Creating empty properties collection if it is null
		
		options.put(RecordManagerOptions.INDEX_RELATIVE_PATH_OPTION, indexDirectory + "/" + projectName + ".idx");
		
		_recordManager = RecordManagerFactory.createRecordManager(projectName , options);
		
		initTables(projectName);
		initListeners();
		
		logMessage(SRC+".ctor" , "Instantiation of PersistenceManager is done. Index Directory : "+ _indexDirectory );
	}

	/**
	 * Initializes Symbol Tables for {@code projectName} Project
	 * 
	 * @param projectName name of the Project 
	 */
	private void initTables(String projectName) {
		
		_spxUnitsTable   = new SpxCompilationUnitTable(this);
		_spxPackageTable = new SpxPackageLookupTable(this);
		_spxModuleTable  = new SpxModuleLookupTable(this);
	
		// TODO : add primary symbol table
	}
	
	
	/**
	 * Initializes RecordListeners
	 */
	private void initListeners()
	{
		// chain record listeners among the tables to keep the consistency in inter-table symbols. 
		// If compilation unit is removed, automatically remove packages.
		// If package is removed , automatically remove the modules that is located 
		// in that particular compilation units 
		
		_spxUnitsTable.addRecordListener((ICompilationUnitRecordListener)_spxPackageTable);
		_spxUnitsTable.addRecordListener((ICompilationUnitRecordListener)_spxModuleTable);
		
		
		//TODO : Chain package removed event so that whenever a package is removed from
		//symbol table, remove enclosing module declaration.  Currently it is linked with 
		//the record listener of the SpxCompilationUnit. Hence, whenever a SpxCompilationUnit
		//is updated , it updates both package and module table and underlying symbols .
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

	public void clearAll() throws IOException{
		try
		{
			this._spxUnitsTable.clear();	
			this._spxPackageTable.clear();
			this._spxModuleTable.clear();
			
			logMessage(SRC + ".clearAll", "SymbolTable is cleaned successfully. ");
		}catch(IOException ex)
		{
			logMessage(SRC + ".clearAll", "Exception occured . "+ ex);
			throw ex;
		}
		
		
	}

	/* Logs Message 
	 * 
	 * @see org.spoofax.interpreter.library.language.spxlang.ISpxPersistenceManager#logMessage(java.lang.String, java.lang.String)
	 */
	public void logMessage(String origin, String message) {
		if(DEBUG)
		{		
			try {
				_agent.getWriter(IOAgent.CONST_STDOUT).write(
						"[" + this._projectName + "." + origin + "]   " + message
								+ "\n");
			} 
			catch (IOException e) {
				
			}
		}
	}

	public String getProjectName() {
		return _projectName;
	}
	
}
