package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.IOException;

import jdbm.PrimaryHashMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.core.Tools;
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
	 * Registry keeps the mapping of projectPath to its own facade
	 * to create and perform various operations related to semantic index.
	 */
	private final SpxSemanticIndexFacadeRegistry _facadeRegistry;
		
	public SpxSemanticIndex(){
		_facadeRegistry = new SpxSemanticIndexFacadeRegistry();
	}
	
	public SpxSemanticIndexFacadeRegistry getFacadeRegistry() {
		return _facadeRegistry;
	}
			
	public boolean indexImportReferences(IStrategoString projectPath,
			final IStrategoAppl importReferences) throws Exception {
	
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectPath, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
				idxFacade.indexImportReferences(importReferences);
			}
		};
		
		return idx.executeIndexer(projectPath, importReferences);
	}
	
	public boolean indexSymbolDefinition(IStrategoString projectPath, final IStrategoAppl symbolDef)  throws Exception{
		
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectPath, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
				idxFacade.indexSymbol(symbolDef);
			}
		};
		return idx.executeIndexer(projectPath, symbolDef);
	}
	
	// Index module definition . 
	public boolean indexModuleDefinition(IStrategoString projectPath, final IStrategoAppl moduleDefinition) throws Exception
	{
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectPath, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
				idxFacade.indexModuleDefinition(moduleDefinition);
			}
		};
		return idx.executeIndexer(projectPath, moduleDefinition);
	}
	
	public boolean indexPackageDeclaration(IStrategoString projectPath, final IStrategoAppl packageDecl) throws Exception {
		SpxIndexer idx = new SpxIndexer() {
			public void index(IStrategoString projectPath, IStrategoAppl appl) throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
				idxFacade.indexPackageDeclaration(packageDecl);
				}
		};

		return idx.executeIndexer(projectPath, packageDecl);
	}

	public boolean indexLanguageDescriptor(IStrategoString projectPath,	final IStrategoAppl languageDescriptor) throws Exception {
		return new SpxIndexer() {
			public void index(IStrategoString projectPath, IStrategoAppl appl)
					throws Exception {
				SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
				idxFacade.indexLanguageDescriptor(languageDescriptor);
			}
		}.executeIndexer(projectPath, languageDescriptor);
	}

	public boolean indexCompilationUnit(IStrategoString projectPath, IStrategoString spxCompilationUnitPath, IStrategoAppl spxCompilationUnitAST) throws IllegalStateException, Exception{

		SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
		idxFacade.indexCompilationUnit( spxCompilationUnitPath, spxCompilationUnitAST);
		return true;
	}
	
	public IStrategoTerm getCompilationUnit(IStrategoString projectPath, IStrategoString spxCompilationUnitPath) throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm term)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getCompilationUnit((IStrategoString)term);
			}
		}.executeWith(projectPath, spxCompilationUnitPath);
	}
	
	public IStrategoTerm getPackageDeclaration(IStrategoString projectPath, final IStrategoAppl packageTypedQname) throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm term)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getPackageDeclaration((IStrategoAppl)term);
			}
		}.executeWith(projectPath, packageTypedQname);
		
	}

	public IStrategoTerm getPackageDeclarationsByUri(IStrategoString projectPath, IStrategoString compilationUnitUri)  throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm term)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getPackageDeclarations((IStrategoString) term);
			}
		}.executeWith(projectPath, compilationUnitUri);
	}

	public IStrategoTerm getModuleDeclaration(IStrategoString projectPath, final IStrategoAppl moduleTypedQname) throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm qname)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getModuleDeclaration((IStrategoAppl)qname);
			}
		}.executeWith(projectPath, moduleTypedQname);
	}
	
	public IStrategoTerm getImports(IStrategoString projectPath, final IStrategoAppl namespaceID) throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm namespaceID)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getImportReferences((IStrategoAppl)namespaceID);
			}
		}.executeWith(projectPath, namespaceID);
	}
	
	public IStrategoTerm getModuleDeclarations(IStrategoString projectPath, IStrategoTerm retTerm) throws Exception {
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm res)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getModuleDeclarationsOf(res);
			}
		}.executeWith(projectPath, retTerm);
	}

	public IStrategoTerm insertNewScope(IStrategoString projectPath, IStrategoAppl namespaceAppl) throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm ns)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.insertNewScope((IStrategoAppl)ns);
			}
		}.executeWith(projectPath, namespaceAppl);
	}	
	
	public IStrategoTerm destroyScope(IStrategoString projectPath, IStrategoAppl namespaceAppl) throws Exception {
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm ns)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.destroyScope((IStrategoAppl)ns);
			}
		}.executeWith(projectPath, namespaceAppl);
	}

	public IStrategoTerm getModuleDefinition(IStrategoString projectPath, final IStrategoAppl moduleTypedQname) throws Exception {
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm qname)
					throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getModuleDefinition((IStrategoAppl)qname);
			}
		}.executeWith(projectPath, moduleTypedQname);
	}
	
	public IStrategoTerm getLanguageDescriptor(IStrategoString projectPath, final IStrategoAppl packageTypedQname) throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm qname) throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.getLanguageDescriptor((IStrategoAppl)qname);
			}
		}.executeWith(projectPath, packageTypedQname);
	}
	
	public IStrategoTerm resolveSymbols(IStrategoString projectPath, final IStrategoTuple searchCriteria)  throws Exception{
		return new SpxResolver() {
			@Override
			public IStrategoTerm resolve(IStrategoString projectPath, IStrategoTerm qname) throws Exception {	
					
					SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
					return idxFacade.resolveSymbols((IStrategoTuple)qname);
			}
		}.executeWith(projectPath, searchCriteria);
	}

	public boolean removeCompilationUnit(IStrategoString projectPath,IStrategoString spxCompilationUnitPath) throws Exception{
	
			SpxSemanticIndexFacade idxFacade = getFacade(projectPath);
			idxFacade.removeCompilationUnit(spxCompilationUnitPath);
			
			return true;
	}
	
	
	SpxSemanticIndexFacade getFacade(IStrategoTerm projectPath) throws Exception {

		SpxSemanticIndexFacade facade = _facadeRegistry.getFacade(projectPath);
		ensureInitialized(facade);
		return facade;
	}
	
	private void ensureInitialized(SpxSemanticIndexFacade idxFactory) throws IllegalStateException {
		if (idxFactory == null)
			throw new IllegalStateException("Spoofaxlang Semantic index not initialized");
	}

	private abstract class SpxIndexer 
	{	
		public boolean executeIndexer(IStrategoString projectPath , IStrategoAppl appl) throws Exception{
			index(projectPath, appl);
			return true;
		}
		
		public abstract void index(IStrategoString projectPath , IStrategoAppl appl) throws Exception ;
	}
	
	private abstract class SpxResolver 
	{	
		public IStrategoTerm executeWith(IStrategoString projectPath , IStrategoTerm term) throws Exception{
			return resolve(projectPath, term);
		}
	
		public abstract IStrategoTerm resolve(IStrategoString projectPath , IStrategoTerm term) throws Exception ;
	}

}
