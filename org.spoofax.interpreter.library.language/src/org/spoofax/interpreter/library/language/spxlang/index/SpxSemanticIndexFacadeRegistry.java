package org.spoofax.interpreter.library.language.spxlang.index;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

//TODO : create a registry that keeps a mapping from 
//project name to the IndexFactory . Current Implementation 
//will still work in multiproject situation - but will be 
//using only one SemanticIndexFactory and require initialization. 
public final class SpxSemanticIndexFacadeRegistry
{
	public static final SpxSemanticIndexFacadeRegistry instance = new SpxSemanticIndexFacadeRegistry();
	private final static Map<String, SpxSemanticIndexFacade> _registry;
	
	static{
		_registry = Collections.synchronizedMap(new HashMap<String, SpxSemanticIndexFacade>());
	}
	
	private SpxSemanticIndexFacadeRegistry(){
	} 
	
	protected void finalize() throws Throwable {
		try {
			if (_registry != null){
				for( SpxSemanticIndexFacade r : _registry.values()){
					r.close(true);
				}
			}
		} catch (Exception e) {
		}
		finally {
			super.finalize();
		}
	}
	
	/**
	 * Initializes the SemanticIndexFactory if the registry does not contain any mapping of existing Facade, or it 
	 * has facade object in the registry , but the underlying persistence manager is closed.
	 * 
	 * @param projectPath
	 * @param termFactory
	 * @throws Exception 
	 */
	public SpxSemanticIndexFacade initFacade(IStrategoTerm projectPath , ITermFactory termFactory , IOAgent agent) throws Exception
	{	
		SpxSemanticIndexFacade fac = null;
		String projectNameString  =  Utils.toAbsPathString(asJavaString(projectPath));
		boolean updateMap = true;
		
		if ( !containsFacade(projectPath)) {
			fac = SpxSemanticIndexFacade.getInstance(projectPath, termFactory, agent) ;
		}	
		else {
			fac = _registry.get(projectNameString);
			
			if( fac == null){
				fac = SpxSemanticIndexFacade.getInstance(projectPath, termFactory, agent) ;
			}
			else
			{
				if(fac.isPersistenceManagerClosed())
					fac.tryReInitializePersistenceManager();
				else
					updateMap = false;
			}
		}
		
		if(updateMap)
			_registry.put(fac.getProjectPath(), fac);
		
		Utils.assertIsNotNull(fac, "Unexpected error . Facade is null for "+ projectNameString );
		
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
		String key = Utils.toAbsPathString(asJavaString(projectPath));		
		SpxSemanticIndexFacade facade =  _registry.get(key);
		
		if(facade == null) {
			throw new IllegalStateException("Symbol Table is not initialized for project : " + projectPath + " . Invoke SPX_index_init. ");
		}	
		else{
			facade.tryReInitializePersistenceManager();
		}	
		
		return facade;
	}

	public void clearAll() throws IOException{
		for(String fname : _registry.keySet())
			removeFacade(Utils.toAbsPathString(fname));
	}
	
	private SpxSemanticIndexFacade closePersistenceManager(String projectPath) throws IOException{
		SpxSemanticIndexFacade facade = _registry.get(projectPath);
		
		if((facade != null) &&  !facade.isPersistenceManagerClosed())
			facade.close(false);
		
		
		return facade;
	}
	
	public SpxSemanticIndexFacade closePersistenceManager(IStrategoTerm projectPathTerm) throws IOException {
		return closePersistenceManager(Utils.toAbsPathString(asJavaString(projectPathTerm)));
	}

	private SpxSemanticIndexFacade removeFacade(String projectPath) throws IOException {
		closePersistenceManager(projectPath);
		return _registry.remove(projectPath);
	}
	
	
	public boolean containsFacade(IStrategoTerm projectPath){
		String key = Utils.toAbsPathString(asJavaString(projectPath));
		
		return _registry.containsKey(key);
	}	
	
}