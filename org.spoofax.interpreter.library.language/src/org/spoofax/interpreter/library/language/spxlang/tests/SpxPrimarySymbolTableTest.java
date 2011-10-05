package org.spoofax.interpreter.library.language.spxlang.tests;

import java.io.IOException;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.GlobalNamespace;
import org.spoofax.interpreter.library.language.spxlang.INamespace;
import org.spoofax.interpreter.library.language.spxlang.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.NamespaceUri;
import org.spoofax.interpreter.library.language.spxlang.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.PackageNamespace;
import org.spoofax.interpreter.library.language.spxlang.SpxPrimarySymbolTable;
import org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade;
import org.spoofax.interpreter.library.language.spxlang.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.test.AbstractInterpreterTest;

public class SpxPrimarySymbolTableTest extends AbstractInterpreterTest{
	
	private final String _projectName = "_test-symbol-table";
	
	private IStrategoString projectNameTerm; 
	private SpxSemanticIndexFacade _facade;
	private SpxPrimarySymbolTable symbol_table;
	
	final String absPathString1 = "c:/temp/file1.spx" ;
	final String absPathString2 = "c:/temp/file2.spx" ;

	private Interpreter interpreter(){ return itp;	}

	private IOAgent ioAgent() { return itp.getIOAgent(); }
	
	private ITermFactory termFactory() { return factory; 	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp("C:/work/projects/spoofax/spx-imp/source-codes/trunk/org.strategoxt.imp.editors.spoofax/include");
		interpreter().addOperatorRegistry(new LanguageLibrary());
		
		projectNameTerm = termFactory().makeString(_projectName);
	
		_facade = new SpxSemanticIndexFacade(projectNameTerm , termFactory() , ioAgent());
		_facade.reinitSymbolTable();
		
		
		symbol_table = _facade.persistenceManager().spxSymbolTable();
	}

	@Override 
	protected void tearDown() throws Exception { 
		_facade.close(); 
	}
	
	private void indexCompilationUnit() throws IOException
	{	
		ITermFactory f = termFactory() ;
		
		_facade.indexCompilationUnit(f.makeString(absPathString1),
				(IStrategoAppl) getCompilationUnit(f));
		
		_facade.indexCompilationUnit(f.makeString(absPathString2),
				(IStrategoAppl) getCompilationUnit(f));
	}

	
	private PackageDeclaration packageDeclaration1;
	private PackageDeclaration packageDeclaration2;
	
	private ModuleDeclaration moduleDeclarationP1M1;
	private ModuleDeclaration moduleDeclarationP1M2;
	private ModuleDeclaration moduleDeclarationP2M1;
	
	private void setupScopeTree() throws IOException, SpxSymbolTableException 
	{
		String packageName1 =  	"\"lang\", \"p1\"" ;
		String packageName2 =  	"\"lang\", \"p2\"" ;
		
		indexCompilationUnit();
		
		// Indexing Package Declaration . That will create a package namespace and an internal namespace
		packageDeclaration1 = indexTestPackageDecl(packageName1, absPathString1);
		packageDeclaration2 = indexTestPackageDecl(packageName2, absPathString2);
		
		moduleDeclarationP1M1 = indexTestModuleDefs ( "p1m1" , packageName1 , absPathString1);
		moduleDeclarationP1M2 = indexTestModuleDefs ( "p1m2" , packageName1 , absPathString1);
		moduleDeclarationP2M1 = indexTestModuleDefs ( "p2m1" , packageName2 , absPathString2);
	}

	/**
	 * Validates that Global Namespace is defined only once 
	 */
	public void testOnlyOneGlobalNamespaceExists() {
		
		assertEquals(1, noOfGlobalNamespaceInSymbolTable());
		
		//Trying to add Global Namespace Multiple Times 
		symbol_table.addGlobalNamespace(_facade);
		symbol_table.addGlobalNamespace(_facade);
		symbol_table.addGlobalNamespace(_facade);
		
		//Still expecting there will be just one Global Namespace for the project 
		assertEquals(1, noOfGlobalNamespaceInSymbolTable());
	}
	
	
	/**
	 * Tests resolving Namespace from Symbol-Table 
	 * 
	 * @throws IOException
	 * @throws SpxSymbolTableException
	 */
	public void testResolveNamespace() throws IOException, SpxSymbolTableException
	{
		setupScopeTree();
		
		INamespace namespace;
		NamespaceUri internalNamespaceUri;
		
		namespace = symbol_table.resolveNamespace(packageDeclaration1.getId());
		assertTrue(namespace.namespaceUri().id().equals(packageDeclaration1.getId()));
		
		internalNamespaceUri = PackageNamespace.packageInternalNamespace(namespace.namespaceUri(), _facade);
		namespace = symbol_table.resolveNamespace(internalNamespaceUri.id());
		assertTrue(namespace.namespaceUri().id().equals(internalNamespaceUri.id()));
		
		
		namespace = symbol_table.resolveNamespace(packageDeclaration1.getId());
		assertTrue(namespace.namespaceUri().id().equals(packageDeclaration1.getId()));
		
		internalNamespaceUri = PackageNamespace.packageInternalNamespace(namespace.namespaceUri(), _facade);
		namespace = symbol_table.resolveNamespace(internalNamespaceUri.id());
		assertTrue(namespace.namespaceUri().id().equals(internalNamespaceUri.id()));
		
		namespace = symbol_table.resolveNamespace(packageDeclaration2.getId());
		assertTrue(namespace.namespaceUri().id().equals(packageDeclaration2.getId()));
		
		namespace = symbol_table.resolveNamespace(moduleDeclarationP1M1.getId());
		assertTrue(namespace.namespaceUri().id().equals(moduleDeclarationP1M1.getId()));

		namespace = symbol_table.resolveNamespace(moduleDeclarationP1M2.getId());
		assertTrue(namespace.namespaceUri().id().equals(moduleDeclarationP1M2.getId()));


		namespace = symbol_table.resolveNamespace(moduleDeclarationP2M1.getId());
		assertTrue(namespace.namespaceUri().id().equals(moduleDeclarationP2M1.getId()));


	}
	
	public void testNoOfNamespaceDefined() throws IOException, SpxSymbolTableException {
		setupScopeTree();
		
		// Expected #namespace = 1 global namespace + 2 namespaces for Package p1 
		// +2 namespaces for Package P2 + 2 for Modules of P1 + 1 for Modules of P2 
		assertEquals( 1 + 2 + 2 + 2 + 1 ,symbol_table.size());
	}
	
	public void testUnknownNamespaceShouldThrowSpxSymbolTableException() throws IOException{
			
		// defining following composite ID :  (Global() , "TestId")
		IStrategoAppl namespaceAppl = termFactory().makeAppl(_facade.getGlobalNamespaceTypeCon());
		IStrategoTerm symbolId = termFactory().makeTuple( namespaceAppl , termFactory().makeString("TestId")); 
		IStrategoAppl typeAppl = namespaceAppl ; 
		IStrategoAppl pQnameUnknown = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+"\"lang\", \"unknown\"" +"]))");
		try{
			setupScopeTree();
			_facade.resolveSymbols( 
					termFactory().makeTuple( 
						pQnameUnknown,
						symbolId,
						typeAppl 
					));
			
		}catch(SpxSymbolTableException ex) {}
	}
	public void testDefiningGlobalSymbol() throws IOException, SpxSymbolTableException {
		
		setupScopeTree();

		// defining a composite key 
		IStrategoAppl namespaceAppl = termFactory().makeAppl(_facade.getGlobalNamespaceTypeCon());
		// defining following composite ID :  (Global() , "TestId")
		IStrategoTerm symbolId = termFactory().makeTuple( namespaceAppl , termFactory().makeString("TestId")); 
		// defining Data 
		IStrategoTerm data = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);
		// setting Type to Global() 
		IStrategoAppl typeAppl = namespaceAppl ; 
		
		// Defining Symbol-Table entry 
		IStrategoAppl symbolDef = createEntry(namespaceAppl , symbolId , typeAppl  , data);
		
		// Indexing Symbol
		_facade.indexSymbol(symbolDef);
	
		
		// Resolving Symbol 
		Iterable<IStrategoTerm> resolvedSymbols = 
			_facade.resolveSymbols( 
				termFactory().makeTuple( 
					ModuleDeclaration.toModuleIdTerm(_facade, moduleDeclarationP1M1),
					symbolId,
					typeAppl 
				));
		
		int actualCount = 0 ;
		for( IStrategoTerm t : resolvedSymbols ) { actualCount += 1; }
		assertEquals( 1 , actualCount);
	}
	
	public void testDefiningGlobalSymbols() throws IOException, SpxSymbolTableException {
		setupScopeTree();
	
		IStrategoAppl namespaceAppl = termFactory().makeAppl(_facade.getGlobalNamespaceTypeCon());
		
		IStrategoTerm symbolId1 = termFactory().makeTuple( namespaceAppl , termFactory().makeString("1")); // defining following composite ID :  (Global() , "TestId") 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0)); // setting Type  
		
		IStrategoTerm symbolId2 = symbolId1 ; 
	 	IStrategoTerm data2 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl2 = termFactory().makeAppl(termFactory().makeConstructor("STRDef", 0)); // setting Type  
		
		IStrategoTerm symbolId3 = symbolId1 ; 
	 	IStrategoTerm data3 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl3 = typeAppl1;  
		
		
		// Defining Symbol-Table entries  
		IStrategoAppl symbolDef1 = createEntry(namespaceAppl , symbolId1 , typeAppl1  , data1);
		IStrategoAppl symbolDef2 = createEntry(namespaceAppl , symbolId2 , typeAppl2  , data2);
		IStrategoAppl symbolDef3 = createEntry(namespaceAppl , symbolId3 , typeAppl3  , data3);
		
		_facade.indexSymbol(symbolDef1);
		_facade.indexSymbol(symbolDef2);
		_facade.indexSymbol(symbolDef3);
		
		// Resolving Symbol 
		Iterable<IStrategoTerm> resolvedSymbols =  _facade.resolveSymbols( 
				termFactory().makeTuple( 
				ModuleDeclaration.toModuleIdTerm(_facade, moduleDeclarationP1M1),
				symbolId1,
				typeAppl3 
			));
		
		int actualCount = 0 ;
		for(IStrategoTerm t : resolvedSymbols) { 
			actualCount += 1; 
		}
		
		assertEquals( 2 , actualCount);
	}
	
	private IStrategoAppl createEntry(IStrategoAppl namespaceAppl , IStrategoTerm id , IStrategoAppl typeAppl, IStrategoTerm data){
		
		IStrategoConstructor ctor = _facade.getSymbolTableEntryDefCon();
		IStrategoAppl symbolEntryAppl = (IStrategoAppl)termFactory().makeAppl(ctor, namespaceAppl , id , typeAppl,data);
		return symbolEntryAppl;
	}
	
	static IStrategoTerm getCompilationUnit( ITermFactory f)
	{
		String text = "CompilationUnit("
			+"[]" 
			+", [ Package("
			+"      QName([\"languages\", \"entitylang\"])"
			+"    , [ Module("
			+"          None() "
			+"        , SPXModuleName(\"Entitylang-Descriptor\")"
			+"        , [" 
			+"          ]"
			+"        )"
			+"      ]"
			+"    )"
			+"  ]"
			+")" ;
		
		return f.parseFromString(text);
	}
	
	private IStrategoTerm getId(String idString) { return termFactory().parseFromString(idString);}
	
	private ModuleDeclaration indexTestModuleDefs( String moduleName , String packageQName , String filePath) throws SpxSymbolTableException
	{
		String moduleQName = packageQName  + ", \""+ moduleName  +"\"" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageQName+"]))");
		
		IStrategoAppl mQnameAppl = (IStrategoAppl)termFactory().parseFromString("Module(QName(["+moduleQName+ "]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(termFactory(), moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(termFactory(), moduleName);
		
		_facade.indexModuleDefinition(mQnameAppl, termFactory().makeString(filePath), pQnameAppl, ast, analyzed_ast);
		
		return _facade.lookupModuleDecl(mQnameAppl);
	}
	
	private PackageDeclaration indexTestPackageDecl(String packageName , String fileName) throws SpxSymbolTableException {
		
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageName+"]))");
		_facade.indexPackageDeclaration(pQnameAppl, termFactory().makeString(fileName));
		
		
		return _facade.lookupPackageDecl(pQnameAppl);
	}
	
	private int noOfGlobalNamespaceInSymbolTable() {
		int noOfGlobalNamespace = 0 ;
		
		Iterable<NamespaceUri> uris = symbol_table.getAllNamespaces();
		for( NamespaceUri uri : uris){
			if ( uri.equalSpoofaxId(GlobalNamespace.getGlobalNamespaceId(_facade)))
				noOfGlobalNamespace = noOfGlobalNamespace  +1 ;
		}
		return noOfGlobalNamespace;
	}
}
