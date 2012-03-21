package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.IOException;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;
import jdbm.PrimaryTreeMap;

public interface ISpxPersistenceManager  extends ILogger{

	public void commitAndClose()  throws IOException;
	
	public void close()  throws IOException;
	
	public <V> PrimaryStoreMap <Long, V> loadStoreMap( String storeMapName);
	
	public <K,V> PrimaryHashMap<K,V> loadHashMap ( String mapName);

	
	@SuppressWarnings("rawtypes")
	public <K extends Comparable,V> PrimaryTreeMap<K,V> loadTreeMap ( String mapName);

	public SpxCompilationUnitTable  spxCompilcationUnitTable();

	public SpxPackageLookupTable  spxPackageTable();
	
	public SpxModuleLookupTable spxModuleTable();
	
	public SpxPrimarySymbolTable spxSymbolTable();
	
	public void commit() throws IOException;
	
	public boolean isClosed();
	
	public void clear() throws IOException;
	
	public void rollback() throws IOException;
	
	public String getIndexId();
	
	public void initializeSymbolTables(String projectName , SpxSemanticIndexFacade facade) throws Exception;

	public void clearCache() throws IOException;
}

interface ILogger{ 
	public void logMessage(String origin, String message); 
}