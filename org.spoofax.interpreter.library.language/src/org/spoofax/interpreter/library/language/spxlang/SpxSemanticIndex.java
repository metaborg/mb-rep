package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;


/**
 * Semantic Index to store the symbols of SPXlang projects. 
 *  
 * @author Md. Adil Akhter
 * Created On : Aug 20, 2011
 */
public class SpxSemanticIndex {

	//TODO : generalize and refactor common operations. Only expose few public operations and generalize others.  
	
	/**
	 * Registry keeps the mapping of projectName to its own facade
	 * to create and perform various operations related to semantic index.
	 */
	private final SpxSemanticIndexFacadeRegistry _facadeRegistry;
	
		
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
	 * @throws IOException 
	 */
	public boolean initialize(IStrategoTerm projectName,  ITermFactory termFactory, IOAgent agent)  throws Exception
	{

		try
		{	
			new SpxIndexManageCommand(){
				public void executeCommnad(IStrategoTerm projectName, Object... objects) throws Exception{
					// Adding a new entry of the facade for the project 
					// in the registry. 
					_facadeRegistry.add(projectName, (ITermFactory)objects[0], (IOAgent)objects[1]) ;
					
				}
			}.executeCommnad(projectName, termFactory , agent);
			
			return true; 
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
	
	private boolean executeIndexer(IStrategoString projectName , IStrategoAppl appl , SpxIndexer indexer) throws Exception
	{
		boolean successStatement = false;
		try {
			indexer.index(projectName, appl);
			successStatement  = true;
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
	
	private <T> IStrategoTerm resolve(IStrategoString projectName , IStrategoTerm key , ISymbolResolver<T> resolver) throws Exception
	{
		try {
			return (IStrategoTerm) resolver.get(projectName, key);
		}
		catch(IllegalStateException e)
		{
			tryCleanupResources(projectName);
			throw e;
		}
		catch(Exception ex) { throw ex ;}
		catch(Error er)
		{
			tryCleanupResources(projectName);
			throw er;
		}
	}
	
	public boolean indexImportReferences(IStrategoString projectName,
			final IStrategoAppl importReferences) throws Exception {
	
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectName, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				idxFacade.indexImportReferences(importReferences);
			}
		};
		
		return idx.executeIndexer(projectName, importReferences);
	}
	
	public boolean indexSymbolDefinition(IStrategoString projectName, final IStrategoAppl symbolDef)  throws Exception{
		
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectName, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				idxFacade.indexSymbol(symbolDef);
			}
		};
		
		return idx.executeIndexer(projectName, symbolDef);
	}
	
	// Index module definition . 
	public boolean indexModuleDefinition(IStrategoString projectName, final IStrategoAppl moduleDefinition) throws Exception
	{
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectName, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				idxFacade.indexModuleDefinition(moduleDefinition);
			}
		};
		return executeIndexer(projectName, moduleDefinition,  idx);
	}
	
	public boolean indexPackageDeclaration(IStrategoString projectName, final IStrategoAppl packageDecl) throws Exception {
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectName, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				idxFacade.indexPackageDeclaration(packageDecl);
				}
		};

		return executeIndexer(projectName, packageDecl,  idx);
	}

	public boolean indexLanguageDescriptor(IStrategoString projectName,	final IStrategoAppl languageDescriptor) throws Exception {
		return new SpxIndexer() {
			public void index(IStrategoString projectName, IStrategoAppl appl)
					throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				idxFacade.indexLanguageDescriptor(languageDescriptor);
			}
		}.executeIndexer(projectName, languageDescriptor);
	}
	
	/**
	 * Indexes spoofax lang Compilation Unit
	 * 
	 * @param projectName
	 * @param spxCompilationUnitPath
	 * @param spxCompilationUnitAST
	 * @return true if the CompilationUnit is successfully indexed; otherwise  returns false. 
	 * @throws IllegalStateException
	 * @throws Exception
	 */
	public boolean indexCompilationUnit(IStrategoString projectName, IStrategoString spxCompilationUnitPath, IStrategoAppl spxCompilationUnitAST) throws IllegalStateException, Exception{

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
	
	public IStrategoTerm getCompilationUnit(IStrategoString projectName, IStrategoString spxCompilationUnitPath) throws Exception{
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm key) throws Exception  {
		
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.getCompilationUnit((IStrategoString)key);
			}
		};
		
		return resolve(projectName, spxCompilationUnitPath, resolver);
	}
	
	public IStrategoTerm getPackageDeclaration(IStrategoString projectName, final IStrategoAppl packageTypedQname) throws Exception{
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm qname) throws Exception  {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.getPackageDeclaration((IStrategoAppl)qname);
			}
		};
		
		return resolve(projectName, packageTypedQname, resolver);
	}

	public IStrategoTerm getPackageDeclarationsByUri(IStrategoString projectName, IStrategoString compilationUnitUri)  throws Exception{
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm uri) throws Exception  {
					SpxSemanticIndexFacade idxFacade = getFacade(projectName);
					return idxFacade.getPackageDeclarations((IStrategoString)uri);
			} 
		};
		
		return resolve(projectName, compilationUnitUri, resolver);
	}

	public IStrategoTerm getModuleDeclaration(IStrategoString projectName, final IStrategoAppl moduleTypedQname) throws Exception{
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm qname)  throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.getModuleDeclaration((IStrategoAppl)qname);
			}
		};
		
		return resolve(projectName, moduleTypedQname, resolver);
	}
	
	public IStrategoTerm getImports(IStrategoString projectName, final IStrategoAppl namespaceID) throws Exception{
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm qname)  throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.getImportReferences((IStrategoAppl)namespaceID);
			}
		};
		
		return resolve(projectName, namespaceID, resolver);
	}
	
	public IStrategoTerm getModuleDeclarations(IStrategoString projectName, IStrategoTerm retTerm) throws Exception {
		
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName ,IStrategoTerm res) throws Exception  {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.getModuleDeclarationsOf(res);
			}
		};
		
		return resolve(projectName, retTerm, resolver);
	}
	
	public IStrategoTerm getModuleDefinition(IStrategoString projectName, final IStrategoAppl moduleTypedQname) throws Exception {
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm qname) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.getModuleDefinition((IStrategoAppl)qname);
			}
		};
		
		return resolve(projectName, moduleTypedQname, resolver);
	}
	
	public IStrategoTerm getLanguageDescriptor(IStrategoString projectName, final IStrategoAppl packageTypedQname) throws Exception{
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm qname) throws Exception  {
					SpxSemanticIndexFacade idxFacade = getFacade(projectName);
					return idxFacade.getLanguageDescriptor((IStrategoAppl)qname);
			} 
		};
		
		return resolve(projectName, packageTypedQname, resolver);
	}
	
	public boolean removeCompilationUnit(IStrategoString projectName,IStrategoString spxCompilationUnitPath) throws IllegalStateException, IOException, SpxSymbolTableException{
		
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
	
	public boolean clearall(IStrategoString projectName) throws Exception {
		boolean retValue = false; 
		try
		{
			new SpxIndexManageCommand(){
				public void executeCommnad(IStrategoTerm projectName, Object... objects) throws Exception{
					SpxSemanticIndexFacade idxFacade = getFacade(projectName);
					idxFacade.reinitSymbolTable();

				}
			}.executeCommnad(projectName);
			
			retValue = true;
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
		return retValue;
	}
	/**
	 * Saves the indexes of the project specified by the projectName
	 * 
	 * @param projectName Term representation of the projectName 
	 * @return true if the operation is successful ; otherwise false.
	 * @throws IOException 
	 * @throws SpxSymbolTableException 
	 */
	public boolean save(IStrategoTerm projectName) throws Exception
	{
		boolean retValue = false; 
		try
		{
			new SpxIndexManageCommand(){
				public void executeCommnad(IStrategoTerm projectName, Object... objects) throws Exception{
					SpxSemanticIndexFacade idxFacade = getFacade(projectName);
					idxFacade.persistChanges();

				}
			}.executeCommnad(projectName);
			retValue = true;
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
		return retValue;
	}
	
	public boolean close(IStrategoTerm projectName) throws Exception {
		new SpxIndexManageCommand(){
			public void executeCommnad(IStrategoTerm projectName, Object... objects) throws Exception{
				_facadeRegistry.removeFacade(projectName);

			}
		}.executeCommnad(projectName);

		return true;
	}
	
	
	public void executeCommand( IStrategoTerm commandName, IStrategoTerm projectName, Object...  objects )
	{
		// gets the command from the hashmap indexed by commandname 
		// pass all the arguments 
		// execute the commands 
	}
	/**
	 * Closes any underlying open connection and clean up unmanaged resources. 
	 * 
	 * @param projectName
	 * @throws IOException 
	 */
	private void tryCleanupResources( IStrategoTerm projectName) throws IOException{
		
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
	
	private SpxSemanticIndexFacade getFacade(IStrategoTerm projectName) throws SpxSymbolTableException {

		SpxSemanticIndexFacade facade = _facadeRegistry.getFacade(projectName);
		ensureInitialized(facade);
		return facade;
	}
	
	private void ensureInitialized(SpxSemanticIndexFacade idxFactory) throws IllegalStateException {
		if (idxFactory == null)
			throw new IllegalStateException("Spoofaxlang Semantic index not initialized");
	}

	private abstract class SpxIndexer 
	{	
		public boolean executeIndexer(IStrategoString projectName , IStrategoAppl appl) throws Exception{
			boolean successStatement = false;
			try {
				index(projectName, appl);
				successStatement  = true;
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
		
		public abstract void index(IStrategoString projectName , IStrategoAppl appl) throws Exception ;
	}

	private static abstract class SpxIndexManageCommand 
	{
		public abstract void executeCommnad(IStrategoTerm projectName , Object... objects) throws Exception;
	}

	public IStrategoTerm resolveSymbols(IStrategoString projectName, final IStrategoTuple searchCriteria)  throws Exception{
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm qname) throws Exception  {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.resolveSymbols(searchCriteria);
			}
		};
		
		return resolve(projectName, searchCriteria, resolver);
	}


	public IStrategoTerm resolveSymbol(IStrategoString projectName, final IStrategoTuple searchCriteria) throws Exception {
		ISymbolResolver<IStrategoTerm> resolver = new ISymbolResolver<IStrategoTerm>() {
			public IStrategoTerm get(IStrategoString projectName,IStrategoTerm qname) throws Exception  {
				SpxSemanticIndexFacade idxFacade = getFacade(projectName);
				return idxFacade.resolveSymbol(searchCriteria);
			}
		};
		
		return resolve(projectName, searchCriteria, resolver);
	}	

	
}
