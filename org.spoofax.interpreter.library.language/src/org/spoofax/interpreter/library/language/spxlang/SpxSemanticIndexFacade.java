package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.TermConverter;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.TermAttachmentStripper;

public class SpxSemanticIndexFacade {
	
	private final ISpxPersistenceManager _persistenceManager;
	private final String _projectName ; 
	private final ITermFactory _termFactory;
	private final IOAgent _agent;
	private final TermAttachmentStripper _stripper;
	private final TermConverter _converter;
	private static final String All= "*";
	
	private final HashMap<ConstructorDef , IStrategoConstructor> _knownCons;
	
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
		
		_termFactory = termFactory;
		_agent = agent;
		
		_stripper = new TermAttachmentStripper(_termFactory);
		_converter = new TermConverter(_termFactory);
		_converter.setOriginEnabled(true);
		
		_knownCons = new HashMap<ConstructorDef ,IStrategoConstructor>();
		_persistenceManager = new SpxPersistenceManager(_projectName , _agent.getWorkingDir(),agent);
		
		initKnownConstructors();
	}
	/**
	 * Returns the TermFactory 
	 * @return
	 */
	public ITermFactory getTermFactory() { return _termFactory; }

	public TermConverter getTermConverter() {return _converter ; }
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
	public void indexPackageDeclaration(IStrategoAppl packageDeclaration){
		verifyConstructor(
				packageDeclaration.getConstructor(), 
				getPackageDeclCon(), 
				"Illegal PackageDeclaration");
	
		indexPackageDeclaration(
				(IStrategoAppl)  packageDeclaration.getSubterm(PackageDeclaration.PACKAGE_ID_INDEX), // package id
				(IStrategoString)packageDeclaration.getSubterm(PackageDeclaration.SPX_COMPILATION_UNIT_PATH)  // package location absolute path  
		);
	}

	/**
	 * Indexes {@link PackageDeclaration}
	 * 
	 * @param packageIdAppl 
	 * @param spxCompilationUnitPath
	 */
	public void indexPackageDeclaration(IStrategoAppl packageIdAppl, IStrategoString spxCompilationUnitPath){
		SpxPackageLookupTable table = _persistenceManager.spxPackageTable();
		
		IStrategoList packageId = PackageDeclaration.getPackageId(this, packageIdAppl);
		
		spxCompilationUnitPath  = (IStrategoString)toCompactPositionInfo((IStrategoTerm)spxCompilationUnitPath);
		packageId = (IStrategoList)toCompactPositionInfo((IStrategoTerm)packageId);
		
		if(table.containsPackage(packageId)){
 			table.addPackageDeclarationLocation(
					packageId, 
					toAbsulatePath(spxCompilationUnitPath));
		}else{	
			PackageDeclaration pDecl = new PackageDeclaration(
					toAbsulatePath(spxCompilationUnitPath), 
					packageId);
			table.definePackageDeclaration(pDecl);
		}
	}
	
	/**
	 * @param importReferences
	 */
	public void indexImportReferences(IStrategoAppl importReferences) {
		
		IStrategoAppl namespaceId = (IStrategoAppl) importReferences.getSubterm(0);
		IStrategoList imports = (IStrategoList) importReferences.getSubterm(1);
		
		if (namespaceId.getConstructor() == getModuleQNameCon()) {
			ModuleDeclaration moduleDecl = lookupModuleDecl(namespaceId);
			
			moduleDecl.addImportRefernces(imports);
			
			getPersistenceManager().spxModuleTable().define(moduleDecl);
		} else if (namespaceId.getConstructor() == getPackageQNameCon()) {

			PackageDeclaration pDecl = this.lookupPackageDecl(namespaceId);
			
			pDecl.addImportRefernces(imports);
			
			getPersistenceManager().spxPackageTable().definePackageDeclaration(	pDecl);
		} else
			throw new IllegalArgumentException("Unknown Namespace "	+ namespaceId.toString());
	}

	public IStrategoTerm getImportReferences(IStrategoAppl namespaceId) {
		IdentifiableConstruct ns; 

		if (namespaceId.getConstructor() == getModuleQNameCon()) {
			ns = lookupModuleDecl(namespaceId);
		} else if (namespaceId.getConstructor() == getPackageQNameCon()) {
			ns = this.lookupPackageDecl(namespaceId);
		} else
			throw new IllegalArgumentException("Unknown Namespace "	+ namespaceId.toString());
		
		return ns.getImports(this);
	}

	/**
	 * Indexes LanguageDescriptor for a particular Package specified in {@code langaugeDescriptor}
	 * 
	 * @param languageDescriptor
	 */
	public void indexLanguageDescriptor (IStrategoAppl languageDescriptor)
	{
		verifyConstructor(languageDescriptor.getConstructor(), getLanguageDescriptorCon(), "Invalid LanguageDescriptor argument : "+ languageDescriptor.toString());

		IStrategoList qualifiedPackageId = PackageDeclaration.getPackageId(this, (IStrategoAppl)languageDescriptor.getSubterm(0)) ;
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
		PackageDeclaration decl = lookupPackageDecl(packageTypedQName);
		
		return decl.toTerm(this);
	}

	
	/**
	 * @param packageTypedQName
	 * @return
	 * @throws IllegalArgumentException
	 */
	private PackageDeclaration lookupPackageDecl(IStrategoAppl packageTypedQName) throws IllegalArgumentException {
		
		SpxPackageLookupTable table = getPersistenceManager().spxPackageTable();
		IStrategoList packageId = PackageDeclaration.getPackageId(this, packageTypedQName);
		PackageDeclaration decl = table.getPackageDeclaration(packageId);
		
		if (decl == null)
			throw new IllegalArgumentException( "Unknown Package Id"+ packageTypedQName.toString());
		
		return decl;
	}
	
	public IStrategoList getPackageDeclarations(IStrategoString filePath) {

		logMessage("getPackageDeclarationsByUri | Arguments : " + filePath);

		SpxPackageLookupTable table = getPersistenceManager().spxPackageTable();
		String filepathString = asJavaString(filePath);
		
		Iterable<PackageDeclaration> decls; 
		if(All == filepathString) {
			decls = table.getPackageDeclarations();  //returning all the package declarations found in the current project
		}else{

			String absFilePath = toAbsulatePath(filePath);
			table.verifyUriExists(absFilePath);
			decls = table.packageDeclarationsByUri(absFilePath);
		}
		
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
		ModuleDeclaration decl = lookupModuleDecl(moduleTypeQName);

		return decl.toTerm(this);
	}

	/**
	 * @param moduleTypeQName
	 * @return
	 */
	private ModuleDeclaration lookupModuleDecl(IStrategoAppl moduleTypeQName) {
		
		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		
		IStrategoList moduleId = ModuleDeclaration.getModuleId(this, moduleTypeQName);
		
		ModuleDeclaration decl = table.getModuleDeclaration(moduleId);
		
		if (decl == null)
			throw new IllegalArgumentException( "Unknown Module Id"+ moduleTypeQName.toString());
		
		
		return decl;
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

	public IStrategoList getModuleDeclarations (IStrategoString filePath){
		logMessage("getModuleDeclarations | Arguments : " + filePath);
		
		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		String filepathString = asJavaString(filePath);
		
		Iterable<ModuleDeclaration> decls; 
		if(All == filepathString) {
			decls = table.getModuleDeclarations();  //returning all the package declarations found in the current project
		}else{	
			String absFilePath = toAbsulatePath(filePath);
			table.verifyUriExists(absFilePath);
			decls = table.getModuleDeclarationsByUri(absFilePath);
		}
		
		IStrategoList result =  ModuleDeclaration.toTerm(this, decls);
		logMessage("getModuleDeclarations | Returning IStrategoList : " + result );
		return result;
	}

	public IStrategoList getModuleDeclarations(IStrategoAppl packageQName) {
		logMessage("getModuleDeclarations | Arguments : " + packageQName);
		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		IStrategoList packageID = PackageDeclaration.getPackageId(this, packageQName);
		
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
		ModuleDeclaration decl = lookupModuleDecl(moduleTypedQName);

		SpxModuleLookupTable table = getPersistenceManager().spxModuleTable();
		
		IStrategoList qualifiedModuleId = ModuleDeclaration.getModuleId(this, moduleTypedQName);
		IStrategoTerm moduleAterm =table.getModuleDefinition(qualifiedModuleId) ;
		IStrategoTerm moduleAnnotatedAterm  = table.getAnalyzedModuleDefinition(qualifiedModuleId);
		
		return new ModuleDefinition( decl , (IStrategoAppl)moduleAterm, (IStrategoAppl)moduleAnnotatedAterm).toTerm(this);
	}
	
	/**
	 * Returns {@link LanguageDescriptor} for Spoofaxlang package with {@link packageTypedQName}}
	 *  
	 * @param packageTypedQName
	 * @return {@link IStrategoTerm} representation of {@link IStrategoTerm}
	 * @throws IllegalArgumentException if the package id is not found in the symbol table 
	 * @throws Exception  If package Id is valid but does not have any language descriptor registered
	 */
	public IStrategoTerm getLanguageDescriptor ( IStrategoAppl packageTypedQName) throws IllegalArgumentException, Exception{
		IStrategoList  packageQName = PackageDeclaration.getPackageId(this, packageTypedQName);

		SpxPackageLookupTable table = getPersistenceManager().spxPackageTable();
		table.verifyPackageIDExists(packageQName) ;
		
		LanguageDescriptor desc = table.getLangaugeDescriptor(packageQName);
		if ( desc == null){	
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
			IStrategoList packageId){
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
		verifyConstructor(moduleDefinition.getConstructor() , getModuleDefCon() , "Illegal Module Definition" );
		
		indexModuleDefinition(
				(IStrategoAppl) moduleDefinition.getSubterm(ModuleDeclaration.ModuleTypedQNameIndex),
				(IStrategoString) moduleDefinition.getSubterm(ModuleDeclaration.ModulePathIndex),
				(IStrategoAppl) moduleDefinition.getSubterm(ModuleDeclaration.PackageTypedQNameIndex),
				(IStrategoAppl) moduleDefinition.getSubterm(ModuleDeclaration.AstIndex),
				(IStrategoAppl) moduleDefinition.getSubterm(ModuleDeclaration.AnalyzedAstIndex));
	}

	/**
	 * Indexes Module Definition, e.g. ModuleDef :  Module * String * Package * Term * Term -> Def
	 * 
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

		IStrategoList moduleId = ModuleDeclaration.getModuleId( this, moduleQName);
		IStrategoList packageId = PackageDeclaration.getPackageId(this, packageQName);
		
		_persistenceManager.spxPackageTable().verifyPackageIDExists(packageId) ;
		
		moduleId = (IStrategoList) toCompactPositionInfo(moduleId);
		packageId = (IStrategoList) toCompactPositionInfo(packageId);
		ast = (IStrategoAppl) ast;
		analyzedAst = (IStrategoAppl)analyzedAst;
		spxCompilationUnitPath = (IStrategoString) spxCompilationUnitPath;

		
		table.define(new ModuleDeclaration(toAbsulatePath(spxCompilationUnitPath), moduleId, packageId), ast, analyzedAst);
	}
	
	/**
	 * Saves(Commits) any unsaved data. 
	 *  
	 * @throws IOException
	 */
	public void persistChanges() throws IOException {  _persistenceManager.commit(); }
	
	/**
	 * Closes any underlying open connection. 
	 *  
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (!isPersistenceManagerClosed()) {
			logMessage("close | closing underlying persistence manager instance.");
			_persistenceManager.commitAndClose();
		}else {
			logMessage("close | underlying persistence manager is already closed. ");
		}	
	}	

	/**
	 * Re-initialize Symbol Tables . It clears all the existing entries from  
	 * symbol tables.
	 * 
	 * @throws IOException
	 */
	public void reinitSymbolTable() throws IOException {	
		if (! isPersistenceManagerClosed())
			_persistenceManager.clearAll();
	}

	
	/**
	 * Checks whether the underlying persistence manager is already open. 
	 * 
	 * @return true if PersistenceManage is open. Otherwise returns false.
	 */
	boolean isPersistenceManagerClosed() { 	return _persistenceManager.IsClosed(); }


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

	private IStrategoTerm strip(IStrategoTerm term) { return _stripper.strip(term); 	}
	
	/**
	 * Returns the Absolute Path of the given URI 
	 * 
	 * @param uri URI of the Resource. 
	 * @return Absolute Path represented by the URI  
	 */
	private String toAbsulatePath( IStrategoString uri)
	{
		URI resUri = toFileURI(uri);
		
		return new File(resUri).getAbsolutePath().trim();
	}

	/**
	 * Returns URI 
	 * @param path
	 * @return
	 */
	private URI toFileURI(String path)
	{
		File file = new File(path);
		return	file.isAbsolute()? file.toURI()
			  			 : new File(_agent.getWorkingDir(), path).toURI();
	}

	private URI toFileURI(IStrategoTerm filePath) {	return toFileURI(Tools.asJavaString(filePath)); }

	/**
	 * Verify type of declaration . 
	 * 
	 * @param actual
	 * @param expected
	 * @param message
	 */
	public  void verifyConstructor( IStrategoConstructor actual , IStrategoConstructor expected , String message){
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
	
	public IStrategoConstructor getPackageDeclCon() { return _knownCons.get(ConstructorDef.newInstance("PackageDecl",2)); }
	
	public IStrategoConstructor getModuleDeclCon() { return _knownCons.get(ConstructorDef.newInstance("ModuleDecl", 3));  }

	public IStrategoConstructor getModuleDefCon() {	return _knownCons.get(ConstructorDef.newInstance("ModuleDef" , 5)); }

	public IStrategoConstructor getLanguageDescriptorCon() { return _knownCons.get(ConstructorDef.newInstance("LanguageDescriptor" , 5));}

	public IStrategoConstructor getModuleQNameCon() {return _knownCons.get(ConstructorDef.newInstance("Module" , 1));}

	public IStrategoConstructor getPackageQNameCon() {return _knownCons.get(ConstructorDef.newInstance("Package" , 1));}
	
	public IStrategoConstructor getQNameCon() {return _knownCons.get(ConstructorDef.newInstance("QName" , 1));}
	
	public IStrategoConstructor getImportDeclCon() {return _knownCons.get(ConstructorDef.newInstance("ImportDecl",2));}
	
	
	private void initKnownConstructors() {
		ConstructorDef.newInstance("ModuleDef" , 5).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("ModuleDecl", 3).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("PackageDecl",2).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("ImportDecl",2).index(_knownCons, _termFactory);	
		ConstructorDef.newInstance("LanguageDescriptor", 5).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("Module", 1).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("Package", 1).index(_knownCons, _termFactory);
		ConstructorDef.newInstance("QName", 1).index(_knownCons, _termFactory);
	}
	
	private static class ConstructorDef
	{
		private String _name ;
		private int _arity;
		ConstructorDef( String name , int arity) {  _name =  name ; _arity = arity; }
		
		static ConstructorDef newInstance( String name , int arity) {  return new ConstructorDef(name, arity); }
		
		private IStrategoConstructor toStrategoConstructor(ITermFactory fac) {  return fac.makeConstructor(_name, _arity);}
		
		void index( HashMap<ConstructorDef , IStrategoConstructor> cons , ITermFactory fac)
		{
			cons.put(this, this.toStrategoConstructor(fac)) ;
		}

	
		@Override
		public String toString() {
			return "ConstructorDef [_name=" + _name + ", _arity=" + _arity
					+ "]";
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + _arity;
			result = prime * result + ((_name == null) ? 0 : _name.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConstructorDef other = (ConstructorDef) obj;
			if (_arity != other._arity)
				return false;
			if (_name == null) {
				if (other._name != null)
					return false;
			} else if (!_name.equals(other._name))
				return false;
			return true;
		}
		
	}
}
