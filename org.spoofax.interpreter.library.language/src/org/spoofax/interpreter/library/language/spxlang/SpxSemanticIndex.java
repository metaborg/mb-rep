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

	/**
	 * Saves the indexes of the project specified by the projectName
	 * @param projectName Term representation of the projectName 
	 * @return true if the operation is successful ; otherwise false.
	 */
	public boolean save(IStrategoTerm projectName) throws IllegalStateException
	{
		boolean retvalue = false;
		
		SpxSemanticIndexFacade idxFacade = getFacade(projectName);
		try {
			
			idxFacade.persistChanges();
			retvalue = true;
		} catch (Exception e) {
			idxFacade.printError( "[SPX_Index_Save failed] Error : "+ e.getMessage()) ; //e.printStackTrace();
		}

		return retvalue;
	}

	private SpxSemanticIndexFacade getFacade(IStrategoTerm projectName) {

		SpxSemanticIndexFacade facade = _facadeRegistry.getFacade(projectName);
		ensureInitialized(facade);
		return facade;
	}
	
	private void ensureInitialized(SpxSemanticIndexFacade idxFactory) throws IllegalStateException {
		if (idxFactory == null)
			throw new IllegalStateException("Semantic index not initialized");
	}
	
	public boolean indexCompilationUnit(IStrategoString projectName,
			IStrategoString spxCompilationUnitPath,
			IStrategoAppl spxCompilationUnitAST) throws IllegalStateException, Exception{

		boolean successStatement = false;
		
		SpxSemanticIndexFacade idxFacade = getFacade(projectName);
		try {
			idxFacade.indexCompilationUnit( spxCompilationUnitPath, spxCompilationUnitAST);
			successStatement = true; // setting the flag to indicate the operation is successful
		}
		catch (Exception ex)
		{	
			idxFacade.printError( "[SPX_Index_Save failed] Error : "+ ex.getMessage()) ;	//logging exception.
		}
		return successStatement;
	}

}
