package org.spoofax.interpreter.library.language.spxlang.index;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

//TODO : create a registry that keeps a mapping from 
//project name to the IndexFactory . Current Implementation 
//will still work in multiproject situation - but will be 
//using only one SemanticIndexFactory and require initialization. 
public class SpxSemanticIndexFacadeRegistry
{
	final static Map<String, SpxSemanticIndexFacade> _registry;
	
	static {
		_registry = Collections.synchronizedMap(new HashMap<String, SpxSemanticIndexFacade>());
	}
	
	/**
	 * Initializes the SemanticIndexFactory if the registry does not contain any mapping of existing Facade, or it 
	 * has facade object in the registry , but the underlying persistence manager is closed.
	 * 
	 * @param projectPath
	 * @param termFactory
	 * @throws Exception 
	 */
	public synchronized SpxSemanticIndexFacade initFacade(IStrategoTerm projectPath , ITermFactory termFactory , IOAgent agent) throws Exception
	{	
		SpxSemanticIndexFacade fac = null;
		String projectNameString  =  SpxIndexUtils.toAbsPathString(asJavaString(projectPath));

		if ( !containsFacade(projectPath)) {
		 
			fac = new SpxSemanticIndexFacade(projectPath, termFactory, agent);
			fac.initializePersistenceManager();
			
			// when facade is first time initializing, it cleans up
			// index and symbol table so that every thing get re-indexed
			fac.cleanIndexAndSymbolTable();
		}	
		else {
			fac = _registry.get(projectNameString);
			// Checks to verify whether the persistence manager is closed. 
			// If it is , creating a new instance of PersistenceManager.
			if( (fac != null) && fac.isPersistenceManagerClosed()){
				fac.initializePersistenceManager();
			}else if ( fac == null){
				fac = new SpxSemanticIndexFacade(projectPath, termFactory, agent);
				fac.initializePersistenceManager();
			}	
		}
		
		if(fac != null){
			_registry.put(fac.getProjectPath(), fac);
		}	
		return fac;
	}

	/**
	 * Gets the porject's Semantic Index factory . If it is initialized and somehow is not closed 
	 * then it returns the instance of the factory to perform further operation. 
	 * 
	 * @param projectPath  ProjectName Term
	 * 
	 * @return SpxSemanticIndexFactory mapped with the projectName. If no mapping is found, it is returning null. 
	 * @throws Exception 
	 */
	public SpxSemanticIndexFacade getFacade(IStrategoTerm projectPath) throws Exception{
		String key = SpxIndexUtils.toAbsPathString(asJavaString(projectPath));		
		SpxSemanticIndexFacade facade =  _registry.get(key);
		
		if(facade == null) {
			throw new SpxSymbolTableException("Symbol Table is not initialized for project : " + projectPath + " . Invoke SPX_index_init. ");
		}	
		else if( (facade != null) && facade.isPersistenceManagerClosed()){
			facade.initializePersistenceManager();
		}	
		return facade;
	}

	public synchronized void clearAll() throws IOException{
		for(String fname : _registry.keySet())
			removeFacade(SpxIndexUtils.toAbsPathString(fname));
	}
	
	private SpxSemanticIndexFacade closePersistenceManager(String projectPath) throws IOException{
		SpxSemanticIndexFacade facade = _registry.get(projectPath);
		
		if((facade != null) &&  !facade.isPersistenceManagerClosed())
			facade.close(false);
		
		
		return facade;
	}
	
	public SpxSemanticIndexFacade closePersistenceManager(IStrategoTerm projectPathTerm) throws IOException {
		return closePersistenceManager(SpxIndexUtils.toAbsPathString(asJavaString(projectPathTerm)));
	}

	private SpxSemanticIndexFacade removeFacade(String projectPath) throws IOException {
		closePersistenceManager(projectPath);
		return _registry.remove(projectPath);
	}
	
	
	public boolean containsFacade(IStrategoTerm projectPath){
		String key = SpxIndexUtils.toAbsPathString(asJavaString(projectPath));
		
		return _registry.containsKey(key);
	}	
	
}