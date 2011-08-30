package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;



class SpxSemanticIndexFacade {

	private final ISpxPersistenceManager _persistenceManager;

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
	public ISpxPersistenceManager getPersistenceManager()
	{
		return _persistenceManager;
	}

	/**
	 * @param path
	 * @return
	 */
	URI toFileURI(String path)
	{
		File file = new File(path);
		return file.isAbsolute()
		? file.toURI()
				: new File(_agent.getWorkingDir(), path).toURI();
	}

	URI toFileURI(IStrategoTerm filePath) 
	{
		return toFileURI(Tools.asJavaString(filePath));
	}

	String fromFileURI(URI uri) {
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

	/**
	 * Prints error message
	 * 
	 * @param errMessage
	 */
	public void printError(String errMessage)
	{
		_agent.printError(errMessage);
	}

	/**
	 * Adds CompilationUnit to the symbol table 
	 * 
	 * @param spxCompilationUnitPath path of the SpxCompilation Unit. 
	 * It can be a relative path (  relative to project) or absolute path. 
	 * @param spxCompilationUnitAST SPXCompilationUnit AST 
	 */
	public void indexCompilationUnit(
			IStrategoString spxCompilationUnitPath,
			IStrategoAppl spxCompilationUnitAST) {

		URI resUri = toFileURI(spxCompilationUnitPath); // Converting IStrategoString to File URI 
		
		SpxCompilationUnitSymbolTable table = _persistenceManager.spxCompilcationUnitTable();
		table.define(resUri, spxCompilationUnitAST);
	}

	public IStrategoTerm getCompilationUnit(IStrategoString spxCompilationUnitPath)
	{
		URI resUri = toFileURI(spxCompilationUnitPath);
		
		SpxCompilationUnitSymbolTable table = _persistenceManager.spxCompilcationUnitTable();
		
		return table.get(resUri);
	}
	
	public void removeCompilationUnit( IStrategoString spxCompilationUnitPath )
	{
		URI resUri = toFileURI(spxCompilationUnitPath);
		
		SpxCompilationUnitSymbolTable table = _persistenceManager.spxCompilcationUnitTable();
		
		table.remove(resUri);
	}
}
