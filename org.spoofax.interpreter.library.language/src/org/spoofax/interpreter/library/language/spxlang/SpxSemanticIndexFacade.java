package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.TermAttachmentStripper;

public class SpxSemanticIndexFacade {

	private final ISpxPersistenceManager _persistenceManager;
	private final String _projectName ; 
	private final ITermFactory _termFactory;
	private final IOAgent _agent;
	private final SpxSemanticIndexEntryFactory _entryFactory;
	private final TermAttachmentStripper _stripper;
	
	private final IStrategoConstructor _moduleDefCon;
	private final IStrategoConstructor _moduleDeclCon;
	private final IStrategoConstructor _packageDeclCon;
	private final IStrategoConstructor _languageDescriptorCon;

	/**
	 * Initializes the SemanticIndexFactory
	 * @param projectName name of the project 
	 * @param termFactory {@link ITermFactory}
	 * @param agent {@link IOAgent}
	 * @throws IOException throws {@link IOException} from underlying {@link SpxPersistenceManager}
	 */
	public SpxSemanticIndexFacade(IStrategoTerm projectName , ITermFactory termFactory , IOAgent agent) throws IOException
	{
		_projectName = asJavaString(projectName);
		
		//TODO : consider removing following instance 
		_entryFactory = new SpxSemanticIndexEntryFactory(termFactory);

		_termFactory = termFactory;
		_agent = agent;
		_stripper = new TermAttachmentStripper(_termFactory);
		
		_moduleDefCon  			= _termFactory.makeConstructor("ModuleDef", 5);
		_moduleDeclCon 			= _termFactory.makeConstructor("ModuleDecl", 3);
		_packageDeclCon 		= _termFactory.makeConstructor("PackageDecl", 2);
		_languageDescriptorCon  = _termFactory.makeConstructor("LanguageDescriptor", 5);
		
		_persistenceManager = new SpxPersistenceManager(_projectName , _agent.getWorkingDir(),agent);
	}

	/**
	 * Returns the TermFactory 
	 * @return
	 */
	public ITermFactory getTermFactory() { return _termFactory; }

	/**
	 * Gets the project name as String
	 * @return
	 */
	public String getProjectNameString(){ return _projectName; }

	/**
	 * Get ProjectName as IStrategoTerm
	 * 
	 * @return IStrategoTerm
	 */
	public IStrategoTerm getProjectName(){ return _termFactory.makeString(_projectName);}

	/**
	 * Returns an instance of the Persistence Manager active for the current Facade
	 * @return
	 */
	ISpxPersistenceManager getPersistenceManager(){	return _persistenceManager; }

	
	String fromFileURI(URI uri) {
		File file = new File(uri);
		return file.toString();
	}

	IOAgent getIOAgent() {
		return _agent;
	}
	
	/**
	 * Prints error message
	 * @param errMessage
	 */
	void printError(String errMessage){
		_agent.printError(errMessage);
	}
	
	/**
	 * Force an imploder attachment for a term.
	 * This ensures that there is always some form of position info,
	 * and makes sure that origin info is not added to the term.
	 * (The latter would be bad since we cache in {@link #term}.)
	 */
	public static IStrategoAppl forceImploderAttachment(IStrategoAppl term , URI file) {
		return forceImploderAttachment(term, term, file);
	}
	
	public static IStrategoAppl forceImploderAttachment(IStrategoTerm id, IStrategoAppl term , URI file) {
		
		ImploderAttachment attach = ImploderAttachment.get(id);
		if (attach != null) {
			ImploderAttachment.putImploderAttachment(term, false, attach.getSort(), attach.getLeftToken(), attach.getRightToken());
		} else {
			String fn = file == null ? null : file.toString();
			term.putAttachment(ImploderAttachment.createCompactPositionAttachment(
					fn, 0, 0, 0, -1));
		}
		return term;
	}
	
	/**
	 * Adds CompilationUnit in the symbol table.
	 * 
	 * @param spxCompilationUnitPath path of the SpxCompilation Unit. It can be a relative path (  relative to project) or absolute path. 
	 * @param spxCompilationUnitAST SPXCompilationUnit AST 
	 * @throws IOException 
	 */
	public void indexCompilationUnit( IStrategoString spxCompilationUnitPath, IStrategoAppl spxCompilationUnitAST) throws IOException {

		URI resUri = toFileURI(spxCompilationUnitPath); // Converting IStrategoString to File URI 
		
		IStrategoTerm astTerm = toCompactPositionInfo(spxCompilationUnitAST);

		SpxCompilationUnitTable table = _persistenceManager.spxCompilcationUnitTable();
	
		logMessage("Storing following compilation unit. Path : [" 
					+  spxCompilationUnitPath +"]"
					+ " AST: "+ spxCompilationUnitAST );
		
		table.define(resUri, astTerm);
	}

	/**
	 * Returns CompilationUnit located in {@code spxCompilationUnitPath} as {@link IStrategoTerm}
	 * 
	 * @param spxCompilationUnitPath Location to the CompilationUnit
	 * @return {@link IStrategoTerm} 
	 */
	public IStrategoTerm getCompilationUnit(IStrategoString spxCompilationUnitPath)
	{
		IStrategoAppl retTerm = null; 
		URI resUri = toFileURI(spxCompilationUnitPath);
		
		logMessage("SpxSemanticIndexFacade.getCompilationUnit . Arguments : " + spxCompilationUnitPath);
		
		SpxCompilationUnitTable table = _persistenceManager.spxCompilcationUnitTable();
		
		IStrategoAppl term = (IStrategoAppl)table.get(resUri);
		
		if ( term != null)
			retTerm = forceImploderAttachment(term, resUri);
		
		logMessage("SpxSemanticIndexFacade.getCompilationUnit :  Returning Following APPL for uri " + resUri +  " : "+ retTerm);
		
		return retTerm;
	}
	
	
	/**
	 * Removes CompilationUnit located in {@code spxCompilationUnitPath} file path.  
	 * 
	 * @param spxCompilationUnitPath file path
	 * @throws IOException
	 */
	public void removeCompilationUnit( IStrategoString spxCompilationUnitPath ) throws IOException
	{
		URI resUri = toFileURI(spxCompilationUnitPath);
		
		SpxCompilationUnitTable table = _persistenceManager.spxCompilcationUnitTable();
		
		table.remove(resUri);
	}

	/**
	 * Stores PackageDeclaration in Symbol Table 
	 * 
	 * @param packageDeclaration
	 */
	public void indexPackageDeclaration(IStrategoAppl packageDeclaration)
	{
		assertConstructor(
				packageDeclaration.getConstructor(), 
				getPackageDeclCon(), 
				"Illegal PackageDeclaration");
	
		indexPackageDeclaration(
				(IStrategoAppl)  packageDeclaration.getSubterm(PackageDeclaration.PACKAGE_ID_INDEX), // package id
				(IStrategoString)packageDeclaration.getSubterm(PackageDeclaration.SPX_COMPILATION_UNIT_PATH)  // package location absolute path  
		);
	}
	
	/**
	 * Indexes Spoofaxlang PackageDeclaration 
	 * 
	 * @param packageIdAppl 
	 * @param spxCompilationUnitPath
	 */
	public void indexPackageDeclaration(IStrategoAppl packageIdAppl, IStrategoString spxCompilationUnitPath	)
	{
		SpxPackageLookupTable table = _persistenceManager.spxPackageTable();
		
		IStrategoList packageId = PackageDeclaration.getPackageId(getTermFactory(), packageIdAppl);
		
		spxCompilationUnitPath  = (IStrategoString)toCompactPositionInfo((IStrategoTerm)spxCompilationUnitPath);
		packageId = (IStrategoList)toCompactPositionInfo((IStrategoTerm)packageId);
		
		if(table.containsPackage(packageId))
 			table.addPackageDeclarationLocation(
					packageId, 
					toAbsulatePath(spxCompilationUnitPath));
		else
		{	
			PackageDeclaration pDecl = new PackageDeclaration(
					toAbsulatePath(spxCompilationUnitPath), 
					packageId);
			table.definePackageDeclaration(pDecl);
		}
	}
	
	/**
	 * Indexes LanguageDescriptor for a particular Package specified in {@code langaugeDescriptor}
	 * 
	 * @param languageDescriptor
	 */
	public void indexLanguageDescriptor (IStrategoAppl languageDescriptor)
	{
		assertConstructor(languageDescriptor.getConstructor(), getLanguageDescriptorCon(), "Invalid LanguageDescriptor argument : "+ languageDescriptor.toString());

		IStrategoList qualifiedPackageId = PackageDeclaration.getPackageId(getTermFactory(), (IStrategoAppl)languageDescriptor.getSubterm(0)) ;
		SpxPackageLookupTable table = _persistenceManager.spxPackageTable();

		table.verifyPackageIDExists(qualifiedPackageId) ;

		//TODO : move the following logic to extract information and 
		//construct instance in respective classes . e.g. in LanguageDesrciptor class
		qualifiedPackageId = (IStrategoList)toCompactPositionInfo((IStrategoTerm)qualifiedPackageId);

		IStrategoList lNames = (IStrategoList) this.strip(languageDescriptor.getSubterm(LanguageDescriptor.LanguageNamesIndex));
		IStrategoList lIds = (IStrategoList) this.strip(languageDescriptor.getSubterm(LanguageDescriptor.LanguageIdsIndex));
		IStrategoList lEsvStartSymbols = (IStrategoList) this.strip(languageDescriptor.getSubterm(LanguageDescriptor.EsvStartSymbolsIndex));
		IStrategoList lSDFStartSymbols = (IStrategoList) this.strip(languageDescriptor.getSubterm(LanguageDescriptor.SdfStartSymbolsIndex));

		LanguageDescriptor current = table.getLangaugeDescriptor(qualifiedPackageId);
		if( current != null)
		{	
			current.addEsvDeclaredStartSymbols(this.getTermFactory(), lEsvStartSymbols);
			current.addSDFDeclaredStartSymbols(this.getTermFactory(), lSDFStartSymbols );
			current.addLanguageIDs(this.getTermFactory(), lIds);
			current.addLanguageNames(this.getTermFactory(), lNames);
		}
		else
			current = LanguageDescriptor.newInstance(this.getTermFactory() , qualifiedPackageId , lIds, lNames,lSDFStartSymbols , lEsvStartSymbols);

		table.defineLanguageDescriptor(qualifiedPackageId, current);

	}
	
	/**
	 * Returns the package declaration indexed with {@code packageIdAppl} typed qualified name.
	 * 
	 * @param packageTypedQName
	 * @return
	 * @throws Exception 
	 */
	public IStrategoTerm getPackageDeclaration(IStrategoAppl packageTypedQName) throws IllegalArgumentException
	{
		SpxPackageLookupTable table = getPersistenceManager().spxPackageTable();
		
		IStrategoList packageId = PackageDeclaration.getPackageId(getTermFactory(), packageTypedQName);
		
		PackageDeclaration decl = table.getPackageDeclaration(packageId);
		
		if (decl == null)
			throw new IllegalArgumentException( "Unknown Package Id"+ packageTypedQName.toString());
		
		return decl.toTerm(this);
	}

	public IStrategoList getPackageDeclarationsByUri(IStrategoString uri) {

		logMessage("getPackageDeclarationsByUri | Arguments : " + uri);

		SpxPackageLookupTable table = getPersistenceManager().spxPackageTable();
		String absFilePath = toAbsulatePath(uri);
		table.verifyUriExists(absFilePath);
		Iterable<PackageDeclaration> decls = table.packageDeclarationsByUri(absFilePath);

		IStrategoList result =  PackageDeclaration.toTerm(this, decls);
		logMessage("getPackageDeclarationsByUri | Returning IStrategoList : " + result );

		return result;
	}

	/**
	 * Returns {@link ModuleDeclaration} indexed with Module Id - {@code moduleTypeQName}  
	 * 
	 * @param moduleTypeQName
	 * @return
	 * @throws IllegalArgumentException If {@link ModuleDeclaration} with {@code moduleTypeQName} is not found 
	 */
	public IStrategoTerm getModuleDeclaration(IStrategoAppl moduleTypeQName) throws IllegalArgumentException
	{
		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		
		IStrategoList moduleId = ModuleDeclaration.getModuleId(getTermFactory(), moduleTypeQName);
		
		ModuleDeclaration decl = table.getModuleDeclaration(moduleId);
		
		if (decl == null)
			throw new IllegalArgumentException( "Unknown Module Id"+ moduleTypeQName.toString());
		
		return decl.toTerm(this);
	}
	

	public IStrategoTerm getModuleDeclarationsOf(IStrategoTerm res) {
		
		IStrategoTerm retValue ;
		
		if(Tools.isTermAppl(res))
			retValue  = this.getModuleDeclarations((IStrategoAppl)res);
		else if(Tools.isTermString(res))
			retValue = this.getModuleDeclarations((IStrategoString)res);
		else
			throw new IllegalArgumentException("Unknown argument in getModuleDeclarationOf: " + res);
		
		return retValue;
	}

	public IStrategoList getModuleDeclarations (IStrategoString filePath)
	{
		logMessage("getModuleDeclarations | Arguments : " + filePath);
		
		String absFilePath = toAbsulatePath(filePath);
		
		//TODO : Check abspath is valid 
		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		table.verifyUriExists(absFilePath);
		
		Iterable<ModuleDeclaration> decls = table.getModuleDeclarationsByUri(absFilePath);
		
		IStrategoList result =  ModuleDeclaration.toTerm(this, decls);
		
		logMessage("getModuleDeclarations | Returning IStrategoList : " + result );
		
		return result;
	}

	public IStrategoList getModuleDeclarations(IStrategoAppl packageQName) {
		logMessage("getModuleDeclarations | Arguments : " + packageQName);
		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		IStrategoList packageID = PackageDeclaration.getPackageId(getTermFactory(), packageQName);
		
		_persistenceManager.spxPackageTable().verifyPackageIDExists(packageID ) ;
		
		Iterable<ModuleDeclaration> decls = table.getModuleDeclarationsByPackageId(packageID);
		logMessage("getModuleDeclarations | Found following result from SymbolTable : " + decls);
		IStrategoList result =  ModuleDeclaration.toTerm(this, decls);
		logMessage("getModuleDeclarations | Returning IStrategoList : " + result );
		
		return result;
	}	

	
	/** 
	 * Returns ModuleDefinition for the Module with ID :  {@code moduleTypedQName}
	 * 
	 * @param moduleTypedQName
	 * @return
	 * @throws IllegalArgumentException
	 */
	public IStrategoTerm getModuleDefinition(IStrategoAppl moduleTypedQName) throws IllegalArgumentException
	{
		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		
		IStrategoList qualifiedModuleId = ModuleDeclaration.getModuleId(getTermFactory(), moduleTypedQName);
		
		ModuleDeclaration decl = table.getModuleDeclaration(qualifiedModuleId);
		
		if (decl != null)
		{	
			ModuleDefinition def = new ModuleDefinition( decl , table.getModuleDefinition(qualifiedModuleId) , table.getAnalyzedModuleDefinition(qualifiedModuleId));
			return def.toTerm(this);
		}
		else
			throw new IllegalArgumentException( "Unknown Module Id"+ moduleTypedQName.toString());
	}
	
	
	/**
	 * Returns {@link LanguageDescriptor} for Spoofaxlang package with {@link packageTypedQName}}
	 *  
	 * @param packageTypedQName
	 * @return {@link IStrategoTerm} representation of {@link IStrategoTerm}
	 * @throws IllegalArgumentException if the package id is not found in the symbol table 
	 * @throws Exception  If package Id is valid but does not have any language descriptor registered
	 */
	public IStrategoTerm getLanguageDescriptor ( IStrategoAppl packageTypedQName) throws IllegalArgumentException, Exception
	{
		IStrategoList  packageQName = PackageDeclaration.getPackageId(getTermFactory(), packageTypedQName);
		
		
		SpxPackageLookupTable table = getPersistenceManager().spxPackageTable();
		table.verifyPackageIDExists(packageQName) ;
		
		
		LanguageDescriptor desc = table.getLangaugeDescriptor(packageQName);
		if ( desc == null)
		{	
			throw new SpxSymbolTableException("Not Found LanguageDescriptor for " + packageQName.toString()); 
		}
		return desc.toTerm(this);
	}
	
	
	/**
	 * Removes PackageDeclaration mapped with the {@code spxCompilationUnitPath}
	 * 
	 * @param spxCompilationUnitPath
	 * @param packageId
	 */
	public void removePackageDeclaration(
			IStrategoString spxCompilationUnitPath , 
			IStrategoList packageId)
	{
		SpxPackageLookupTable table = _persistenceManager.spxPackageTable();
		
		spxCompilationUnitPath  = (IStrategoString)toCompactPositionInfo((IStrategoTerm)spxCompilationUnitPath);
		packageId = (IStrategoList)toCompactPositionInfo((IStrategoTerm)packageId);
		
		table.verifyPackageIDExists(packageId) ;
		
		table.removePackageDeclarationLocation(
				packageId, 
				asJavaString(spxCompilationUnitPath));
	}

	
	/**
	 * Indexes {@code moduleDefinition}
	 * 
	 * @param moduleDefinition
	 * @throws IllegalArgumentException
	 */
	public void indexModuleDefinition(IStrategoAppl moduleDefinition) throws IllegalArgumentException
	{
		assertConstructor(moduleDefinition.getConstructor() , _moduleDefCon , "Illegal Module Definition" );
		
		indexModuleDefinition(	(IStrategoAppl)   moduleDefinition.getSubterm(ModuleDeclaration.ModuleTypedQNameIndex), 
								(IStrategoString) moduleDefinition.getSubterm(ModuleDeclaration.ModulePathIndex), 
								(IStrategoAppl)   moduleDefinition.getSubterm(ModuleDeclaration.PackageTypedQNameIndex),
								(IStrategoAppl)   moduleDefinition.getSubterm(ModuleDeclaration.AstIndex),
								(IStrategoAppl)   moduleDefinition.getSubterm(ModuleDeclaration.AnalyzedAstIndex)
								);
	}

	/**
	 * Indexes Module Definition, e.g. ModuleDef :  Module * String * Package * Term * Term -> Def
	 * @param moduleQName
	 * @param spxCompilationUnitPath
	 * @param packageQName
	 * @param ast
	 * @param analyzedAst
	 */
	public void indexModuleDefinition(IStrategoAppl moduleQName,
			IStrategoString spxCompilationUnitPath, IStrategoAppl packageQName,
			IStrategoAppl ast, IStrategoAppl analyzedAst) {

		SpxModuleLookupTable table = _persistenceManager.spxModuleTable();

		IStrategoList moduleId = ModuleDeclaration.getModuleId( this.getTermFactory(), moduleQName);
		IStrategoList packageId = PackageDeclaration.getPackageId(this.getTermFactory(), packageQName);
		
		_persistenceManager.spxPackageTable().verifyPackageIDExists(packageId) ;
		
		moduleId = (IStrategoList) toCompactPositionInfo(moduleId);
		packageId = (IStrategoList) toCompactPositionInfo(packageId);
		ast = (IStrategoAppl) strip(ast);
		analyzedAst = (IStrategoAppl) strip(analyzedAst);
		spxCompilationUnitPath = (IStrategoString) strip(spxCompilationUnitPath);

		// verify whether the enclosing package exists in symbol table
		if (!_persistenceManager.spxPackageTable().containsPackage(packageId))
			throw new IllegalArgumentException("Unknown Package : "
					+ packageId.toString());

		table.define(new ModuleDeclaration(toAbsulatePath(spxCompilationUnitPath), moduleId, packageId), ast, analyzedAst);
	}

	
	/**
	 * Saves(Commits) any unsaved data. 
	 *  
	 * @throws IOException
	 */
	public void persistChanges() throws IOException 
	{
		_persistenceManager.commit();
	}
	
	/**
	 * Closes any underlying open connection. 
	 *  
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (!isPersistenceManagerClosed()) {
			logMessage("close | closing underlying persistence manager instance.");
			_persistenceManager.commitAndClose();
		} else
			logMessage("close | underlying persistence manager is already closed. ");
	}	
	
	public void clearSymbolTable() throws IOException 
	{
		if (! isPersistenceManagerClosed())
			_persistenceManager.clearAll();
	}

	/**
	 * Checks whether the underlying persistence manager is already open. 
	 * 
	 * @return true if PersistenceManage is open. Otherwise returns false.
	 */
	boolean isPersistenceManagerClosed() { 
		return _persistenceManager.IsClosed();
	}
	/**
	 * @return the PackageDeclaration Constructor
	 */
	public IStrategoConstructor getPackageDeclCon() {
		return _packageDeclCon;
	}
	
	public IStrategoConstructor getModuleDeclCon() {
		return _moduleDeclCon;
	}

	/**
	 * @return the ModuleDefinition Constructor
	 */
	public IStrategoConstructor getModuleDefCon() {
		return _moduleDefCon;
	}

	public IStrategoConstructor getLanguageDescriptorCon() {
		return _languageDescriptorCon;
	}
	
	/**
	 * @param spxCompilationUnitAST
	 * @return
	 */
	private IStrategoTerm toCompactPositionInfo(IStrategoTerm term) {
		
		if( term == null) return term;
		
		ImploderAttachment astAttachment = ImploderAttachment.getCompactPositionAttachment(term, true);
		IStrategoTerm astTerm = _stripper.strip(term);
		astTerm.putAttachment(astAttachment);
		return astTerm;
	}

	private IStrategoTerm strip(IStrategoTerm term)
	{
		return _stripper.strip(term);
	}
	
	/**
	 * Returns the Absolute Path of the given URI 
	 * 
	 * @param uri URI of the Resource. 
	 * @return Absolute Path represented by the URI  
	 */
	private String toAbsulatePath( IStrategoString uri)
	{
		URI resUri = toFileURI(uri);
		
		return new File(resUri).getAbsolutePath();
		
	}
	/**
	 * Returns URI 
	 * @param path
	 * @return
	 */
	URI toFileURI(String path)
	{
		File file = new File(path);
		return	file.isAbsolute()? file.toURI()
			  			 : new File(_agent.getWorkingDir(), path).toURI();
	}

	URI toFileURI(IStrategoTerm filePath) 
	{
		return toFileURI(Tools.asJavaString(filePath));
	}

	

	/**
	 * Verify type of declaration . 
	 * 
	 * @param actual
	 * @param expected
	 * @param message
	 */
	public  void assertConstructor( IStrategoConstructor actual , IStrategoConstructor expected , String message)
	{
		if( actual != expected)
			throw new IllegalArgumentException(message);
	}
	
	
	
	/**
	 * Logs message 
	 * 
	 * @param message
	 */
	private void logMessage(String message) {
		
		_persistenceManager.logMessage("SpxSemanticIndexFacade", message);
	}

}
