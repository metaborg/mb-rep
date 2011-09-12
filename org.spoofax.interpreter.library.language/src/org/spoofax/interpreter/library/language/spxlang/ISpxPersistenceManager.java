package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoTerm;

import jdbm.PrimaryHashMap;
import jdbm.PrimaryStoreMap;

public interface ISpxPersistenceManager {

	/**
	 * Commits the unsaved and closes the connection.
	 *  
	 * @throws IOException
	 */
	public void commitAndClose()  throws IOException;
	
	public <V> PrimaryStoreMap <Long, V> loadStoreMap( String storeMapName);
	
	public <K,V> PrimaryHashMap<K,V> loadHashMap ( String mapName);

	public SpxCompilationUnitTable  spxCompilcationUnitTable();

	public SpxPackageLookupTable  spxPackageTable();
	
	public SpxModuleLookupTable spxModuleTable();
	
	public void commit() throws IOException;
	
	public boolean IsClosed();
}