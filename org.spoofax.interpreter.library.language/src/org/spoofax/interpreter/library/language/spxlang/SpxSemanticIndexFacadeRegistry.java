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
	 * Initializes the SemanticIndexFactory
	 * @param projectName
	 * @param factory
	 * @throws IOException
	 */
	public void add(IStrategoTerm projectName , ITermFactory factory , IOAgent agent) throws IOException
	{	
		String projectNameString = asJavaString(projectName);
		
		if ( !_registry.containsKey(projectNameString))
		{
			SpxSemanticIndexFacade fac = new SpxSemanticIndexFacade(projectName, factory, agent);
			
			_registry.put(fac.getProjectNameString(), fac);
		}
	}
	
	
	/**
	 * Gets the porject's Semantic Index factory
	 * @param projectName  ProjectName Term
	 * 
	 * @return SpxSemanticIndexFactory mapped with the projectName. If no mapping is found, it is returning null. 
	 */
	public SpxSemanticIndexFacade getFacade( IStrategoTerm projectName)
	{
		String key = asJavaString(projectName);
		
		return _registry.get(key);
	}
	
	
	public void ClearAll()
	{
		_registry.clear();
	}


	public SpxSemanticIndexFacade removeFacade(IStrategoTerm projectName) {
		String key = asJavaString(projectName);
		
		return _registry.remove(key);
	}
	
	
	public boolean containsFacade(IStrategoTerm projectName)
	{
		String key = asJavaString(projectName);
		
		return _registry.containsKey(key);
	}	
	
}