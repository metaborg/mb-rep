package org.spoofax.interpreter.library.language.spxlang;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.SemanticIndexEntry;
import org.spoofax.interpreter.library.language.SemanticIndexEntryFactory;
import org.spoofax.interpreter.library.language.SemanticIndexEntryParent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * Semantic Index to store the symbols of SPXlang projects. 
 *  
 * @author Md. Adil Akhter
 * Created On : Aug 20, 2011
 */
public class SpxSemanticIndex {

	
	/**
	 * Registry keeps the mapping of projectName to its own facade
	 * to create and perform various operations related to semantic index.
	 */
	private SpxSemanticIndexFacadeRegistry _facadeRegistry;

	public SpxSemanticIndex()
	{
		_facadeRegistry = new SpxSemanticIndexFacadeRegistry();
	}
	
	/**
	 * Initializing Index for the project specified by the projectName
	 * @param projectName Name of the project
	 * @param termFactory Term Factory  
	 * @param agent IOAgent responsible for providing IO operations.
	 * @return true if the operation is successful ; false otherwise.
	 */
	public boolean initialize(IStrategoTerm projectName,  ITermFactory termFactory, IOAgent agent) 
	{
		try
		{	// Adding a new entry of the facade for the project 
			// in the registry. 
			_facadeRegistry.add(projectName, termFactory, agent) ;
			
			return true; 
		}
		catch (IOException ex)
		{
			agent.printError("[SPX_Index_Initialize] Error : "+ex.getMessage());
			
			return false;	
		}
	}


	/**
	 * Adds entry to the Index 
	 * 
	 * @param entry
	 * @param file
	 */
	public void add(String projectName , IStrategoAppl entry, String file)
	{
		// adds entry to the table

		
	}
	
	
	// adds module definition in the index . 
	public void addModuleDefinition(IStrategoTerm moduleDefinition , URI file)
	{
		

	}

	
	public boolean indexCompilationUnit(IStrategoString projectName,
			IStrategoString spxCompilationUnitPath,
			IStrategoAppl spxCompilationUnitAST) throws IllegalStateException, Exception{

		boolean successStatement = false;
		
		try {
			SpxSemanticIndexFacade idxFacade = getFacade(projectName);
			idxFacade.indexCompilationUnit( spxCompilationUnitPath, spxCompilationUnitAST);
			successStatement =  true; // setting the flag to indicate the operation is successful
		}
		catch(IllegalStateException e)
		{
			tryCleanupResources(projectName);
			throw e;
		}
		catch(Error er)
		{
			tryCleanupResources(projectName);
			throw er;
		}	
		return successStatement;
	}

	public IStrategoTerm getCompilationUnit(IStrategoString projectName,
			IStrategoString spxCompilationUnitPath) throws IllegalStateException{
		
		try 
		{
			SpxSemanticIndexFacade idxFacade = getFacade(projectName);
			
			return idxFacade.getCompilationUnit(spxCompilationUnitPath);
		}
		catch(IllegalStateException e)
		{
			tryCleanupResources(projectName);
			throw e;
		}
		catch(Error er)
		{
			tryCleanupResources(projectName);
			throw er;
		}	
	}
	
	public boolean removeCompilationUnit(IStrategoString projectName,
			IStrategoString spxCompilationUnitPath) throws IllegalStateException{
		
		boolean successStatement = false;
		
		
		try {
			SpxSemanticIndexFacade idxFacade = getFacade(projectName);
			idxFacade.removeCompilationUnit(spxCompilationUnitPath);
			
			successStatement = true; // setting the flag to indicate the operation is successful
		}
		catch(IllegalStateException exception)
		{
			tryCleanupResources(projectName);
			throw exception;
		}
		catch(Error er)
		{
			tryCleanupResources(projectName);
			throw er;
		}
		return successStatement;
	}
	
	/**
	 * Saves the indexes of the project specified by the projectName
	 * @param tvars Term representation of the projectName 
	 * @return true if the operation is successful ; otherwise false.
	 * @throws IOException 
	 */
	public boolean save(IStrategoTerm tvars) throws IllegalStateException, IOException
	{
		boolean retValue = false; 
		try
		{
			SpxSemanticIndexFacade idxFacade = getFacade(tvars);
			idxFacade.persistChanges();
			retValue = true;
		}
		catch(IllegalStateException e)
		{
			tryCleanupResources(tvars);
			throw e;
		}
		catch(Error er)
		{
			tryCleanupResources(tvars);
			throw er;
		}
		return retValue;
	}
	
	public boolean close(IStrategoTerm projectName) throws IOException {
		SpxSemanticIndexFacade idxFacade = removeFacade(projectName);
		idxFacade.close();
		return true;
	}
	
	private void tryCleanupResources( IStrategoTerm projectName){
		
		if ( _facadeRegistry.containsFacade(projectName))
		{
			SpxSemanticIndexFacade facade = _facadeRegistry.removeFacade(projectName);
			try {
				facade.close();
			} catch (IOException e) {
				facade.printError( "[SPXSemanticIndex] . Cleanup Failed due to following Error : "+ e.getMessage()) ;	//logging exception.
			}
		}	
	}
	
	private SpxSemanticIndexFacade getFacade(IStrategoTerm projectName) {

		SpxSemanticIndexFacade facade = _facadeRegistry.getFacade(projectName);
		ensureInitialized(facade);
		return facade;
	}
	
	private SpxSemanticIndexFacade removeFacade(IStrategoTerm projectName) {

		SpxSemanticIndexFacade facade = _facadeRegistry.removeFacade(projectName);
		ensureInitialized(facade);
		return facade;
	}
	
	private void ensureInitialized(SpxSemanticIndexFacade idxFactory) throws IllegalStateException {
		if (idxFactory == null)
			throw new IllegalStateException("Spoofaxlang Semantic index not initialized");
	}
	
	
	
}
