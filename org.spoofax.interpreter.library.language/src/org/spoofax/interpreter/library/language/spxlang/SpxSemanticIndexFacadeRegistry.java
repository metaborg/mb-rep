package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.IOException;
import java.util.HashMap;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

//TODO : create a registry that keeps a mapping from 
//project name to the IndexFactory . Current Implementation 
//will still work in multiproject situation - but will be 
//using only one SemanticIndexFactory and require initialization. 
class SpxSemanticIndexFacadeRegistry
{
	
	final HashMap<String, SpxSemanticIndexFacade> _registry = new HashMap<String, SpxSemanticIndexFacade>();
	
	/**
	 * Initializes the SemanticIndexFactory if the registry does not contain any mapping of existing Facade, or it 
	 * has facade object in the registry , but the underlying persistence manager is closed.
	 * 
	 * @param projectName
	 * @param factory
	 * @throws IOException
	 */
	public void add(IStrategoTerm projectName , ITermFactory factory , IOAgent agent) throws IOException
	{	
		SpxSemanticIndexFacade fac = null;
		
		if ( !containsFacade(projectName))
			fac = new SpxSemanticIndexFacade(projectName, factory, agent);
		else
		{
			SpxSemanticIndexFacade f = _registry.get(projectName);
			if( (f!= null) && f.isPersistenceManagerClosed())
			{
				fac = new SpxSemanticIndexFacade(projectName, factory, agent);
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
	public SpxSemanticIndexFacade getFacade( IStrategoTerm projectName) throws SpxSymbolTableException
	{
		String key = asJavaString(projectName);
		
		SpxSemanticIndexFacade facade =  _registry.get(key);
		
		if(facade == null || facade.isPersistenceManagerClosed())
		{
			throw new SpxSymbolTableException("Symbol Table is not initialized for project : " + projectName + " . Invoke SPX_index_init. ");
		}	
		
		return facade;
	}

	public void clearAll() throws IOException{
		for(String fname : _registry.keySet())
			remove(fname);
	}
	
	public SpxSemanticIndexFacade removeFacade(IStrategoTerm projectName) throws IOException {
		String key = asJavaString(projectName);
		
		return remove(key);
	}
	
	private SpxSemanticIndexFacade remove(String projectName) throws IOException {
		
		SpxSemanticIndexFacade facade = _registry.remove(projectName);
		
		if((facade != null) &&  !facade.isPersistenceManagerClosed())
			facade.close();
		
		return facade;
	}
	
	public boolean containsFacade(IStrategoTerm projectName){
		String key = asJavaString(projectName);
		
		return _registry.containsKey(key);
	}	
	
}