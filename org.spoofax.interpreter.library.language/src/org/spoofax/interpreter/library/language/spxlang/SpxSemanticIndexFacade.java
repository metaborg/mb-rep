package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.File;
import java.io.IOException;
import java.net.URI;
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
	
}

class SpxSemanticIndexFacade {

	private final SpxPersistenceManager _persistenceManager;
	
	private final String _projectName ; 
	
	private final ITermFactory _termFactory;
	
	private final IOAgent _agent;
	
	private final SpxSemanticIndexEntryFactory _entryFactory;
	
	/**
	 * Initializes the SemanticIndexFactory
	 * @param projectName
	 * @param termFactory
	 * @throws IOException
	 */
	public SpxSemanticIndexFacade(IStrategoTerm projectName , ITermFactory termFactory , IOAgent agent) throws IOException
	{
		_projectName = asJavaString(projectName);	
		_entryFactory = new SpxSemanticIndexEntryFactory(termFactory);
		
		//Initializes persistent manager
		_persistenceManager = new SpxPersistenceManager(_projectName);
	
		//Sets the Term Factory 
		_termFactory = termFactory;

		//IOAgent to handle URI
		_agent = agent;
	}
	
	/**
	 * Returns the TermFactory 
	 * @return
	 */
	public ITermFactory getTermFactory() {
		return _termFactory;
	}
	
	/**
	 * Gets the project name as String
	 * @return
	 */
	public String getProjectNameString()
	{
		return _projectName;
	}
	
	/**
	 * Get ProjectName as IStrategoTerm
	 * 
	 * @return IStrategoTerm
	 */
	public IStrategoTerm getProjectName()
	{
		return _termFactory.makeString(_projectName);
	}
	
	/**
	 * 
	 * @return
	 */
	public SpxPersistenceManager getPersistenceManager()
	{
		return _persistenceManager;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public URI toFileURI(String path)
	{
		File file = new File(path);
		return file.isAbsolute()
			? file.toURI()
			: new File(_agent.getWorkingDir(), path).toURI();
	}
	
	public String fromFileURI(URI uri) {
		File file = new File(uri);
		return file.toString();
	}

	public IOAgent getIOAgent() {
		return _agent;
	}
		
	public void persistChanges() throws IOException 
	{
		_persistenceManager.commitAndClose();
	}
	
	public void printError(String errMessage)
	{
		_agent.printError(errMessage);
	}
}
