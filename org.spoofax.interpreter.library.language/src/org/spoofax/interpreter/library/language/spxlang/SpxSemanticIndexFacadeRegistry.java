package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.IOException;
import java.util.HashMap;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

//TODO : create a registry that keeps a mapping from 
//project name to the IndexFactory . Current Implementation 
//will still work in multiproject situation - but will be 
//using only one SemanticIndexFactory and require initialization. 
public class SpxSemanticIndexFacadeRegistry
{
	
	final HashMap<String, SpxSemanticIndexFacade> _registry = new HashMap<String, SpxSemanticIndexFacade>();
	
	/**
	 * Initializes the SemanticIndexFactory if the registry does not contain any mapping of existing Facade, or it 
	 * has facade object in the registry , but the underlying persistence manager is closed.
	 * 
	 * @param projectName
	 * @param termFactory
	 * @throws Exception 
	 */
	public void initFacade(IStrategoTerm projectName , ITermFactory termFactory , IOAgent agent) throws Exception
	{	
		SpxSemanticIndexFacade fac = null;
		String projectNameString  = asJavaString(projectName);
		if ( !containsFacade(projectName)) {
			
			fac = new SpxSemanticIndexFacade(projectName, termFactory, agent);
			fac.initializePersistenceManager();
		}	
		else {
			fac = _registry.get(projectNameString);
			// Checks to verify whether the persistence manager is closed. 
			// If it is , creating a new instance of PersistenceManager.
			if( (fac != null) && fac.isPersistenceManagerClosed()){
				fac.initializePersistenceManager();
			}else if ( fac == null){
				fac = new SpxSemanticIndexFacade(projectName, termFactory, agent);
				fac.initializePersistenceManager();
			}	
		}
		
		if(fac != null)
			_registry.put(fac.getProjectNameString(), fac);
	}

	/**
	 * Gets the porject's Semantic Index factory . If it is initialized and somehow is not closed 
	 * then it returns the instance of the factory to perform further operation. 
	 * 
	 * @param projectName  ProjectName Term
	 * 
	 * @return SpxSemanticIndexFactory mapped with the projectName. If no mapping is found, it is returning null. 
	 * @throws SpxSymbolTableException 
	 */
	public SpxSemanticIndexFacade getFacade( IStrategoTerm projectName) throws SpxSymbolTableException{
		String key = asJavaString(projectName);		
		SpxSemanticIndexFacade facade =  _registry.get(key);
		
		if(facade == null || facade.isPersistenceManagerClosed()) {
			throw new SpxSymbolTableException("Symbol Table is not initialized for project : " + projectName + " . Invoke SPX_index_init. ");
		}	
		
		return facade;
	}

	public void clearAll() throws IOException{
		for(String fname : _registry.keySet())
			removeFacade(fname);
	}
	
	private SpxSemanticIndexFacade closePersistenceManager(String projectName) throws IOException{
		SpxSemanticIndexFacade facade = _registry.get(projectName);
		
		if((facade != null) &&  !facade.isPersistenceManagerClosed())
			facade.close();
		
		return facade;
	}
	
	
	
	public SpxSemanticIndexFacade closePersistenceManager(IStrategoTerm projectNameTerm) throws IOException {
		return closePersistenceManager(asJavaString(projectNameTerm));
	}

	private SpxSemanticIndexFacade removeFacade(String projectName) throws IOException {
		
		closePersistenceManager(projectName);
		return _registry.remove(projectName);
	}
	
	
	public boolean containsFacade(IStrategoTerm projectName){
		String key = asJavaString(projectName);
		
		return _registry.containsKey(key);
	}	
	
}