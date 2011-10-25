package org.spoofax.interpreter.library.language.spxlang.index.tests;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.index.GlobalNamespace;
import org.spoofax.interpreter.library.language.spxlang.index.INamespace;
import org.spoofax.interpreter.library.language.spxlang.index.PackageNamespace;
import org.spoofax.interpreter.library.language.spxlang.index.SpxPrimarySymbolTable;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacadeRegistry;
import org.spoofax.interpreter.library.language.spxlang.index.Utils;
import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.NamespaceUri;
import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbol;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolKey;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.test.AbstractInterpreterTest;

public class SpxPrimarySymbolTableTest extends AbstractInterpreterTest{
	
	private final String _projectName = "_test-symbol-table_2";
	
	private IStrategoString projectNameTerm; 
	private SpxSemanticIndexFacade _facade;
	private SpxPrimarySymbolTable symbol_table;
	private SpxSemanticIndexFacadeRegistry _registry;
	final String absPathString1 = "c:/temp/file1.spx" ;
	final String absPathString2 = "c:/temp/file2.spx" ;

	private Interpreter interpreter(){ return itp;	}

	private IOAgent ioAgent() { return itp.getIOAgent(); }
	
	private ITermFactory termFactory() { return factory; 	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp("C:/work/projects/spoofax/spx-imp/source-codes/trunk/org.strategoxt.imp.editors.spoofax/include");
		interpreter().addOperatorRegistry(new LanguageLibrary());
		
		_registry = new SpxSemanticIndexFacadeRegistry();
		
		projectNameTerm = termFactory().makeString(System.getProperty("user.dir")+ "/"+_projectName);
	
		_registry.initFacade(projectNameTerm, termFactory(), ioAgent()); 
		_facade = _registry.getFacade(projectNameTerm);
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
	private PackageDeclaration packageDeclaration3;
	
	private ModuleDeclaration moduleDeclarationP1M1;
	private ModuleDeclaration moduleDeclarationP1M2;
	private ModuleDeclaration moduleDeclarationP2M1;
	private ModuleDeclaration moduleDeclarationP3M1;
	
	private void setupScopeTree() throws IOException, SpxSymbolTableException {
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
	
	
	public void testRemovingPackageDeclarationShouldAlsoRemoveNamespace() throws IOException, SpxSymbolTableException, URISyntaxException{
		
		setupScopeTree();
		
		INamespace ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace(packageDeclaration1.getId());
		assertNotNull(ns);
		
		IStrategoString uriTerm = termFactory().makeString(absPathString1) ;
		_facade.persistenceManager().spxPackageTable().removePackageDeclarationLocation(
				packageDeclaration1.getId(), 
				_facade.toAbsulatePath(uriTerm) 
			);

		ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace(packageDeclaration1.getId());
		assertNull(ns);
	}
	
	public void testRemovingModuleDeclarationShouldAlsoRemoveNamespace() throws IOException, SpxSymbolTableException, URISyntaxException{
		
 		setupScopeTree();
		
		INamespace ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace(moduleDeclarationP1M1.getId());
		assertNotNull(ns);
		
		_facade.persistenceManager().spxModuleTable().remove(moduleDeclarationP1M1.getId());

		ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace(moduleDeclarationP1M1.getId());
		assertNull(ns);
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
						typeAppl,
						termFactory().makeString("*")
					));
			
		}catch(SpxSymbolTableException ex) { }
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
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolId,
				namespaceAppl.getConstructor()
				);
		
		assertEquals( 1 , resolvedSymbols.size());
		
		
	}
	
	public void testInvalidateGlobalCacheRemovesAllEntries() throws IOException, SpxSymbolTableException {
		
		setupScopeTree();

		IStrategoAppl namespaceAppl = termFactory().makeAppl(_facade.getGlobalNamespaceTypeCon());// defining a composite key 
		IStrategoTerm symbolId = termFactory().makeTuple( namespaceAppl , termFactory().makeString("TestId"));// defining following composite ID :  (Global() , "TestId")
	 	IStrategoTerm data = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade); // defining Data
		IStrategoAppl typeAppl = namespaceAppl ;// setting Type to Global() 
		
		// Defining Symbol-Table entry 
		IStrategoAppl symbolDef = createEntry(namespaceAppl , symbolId , typeAppl  , data);
		
		// Indexing Symbol
		_facade.indexSymbol(symbolDef);
	
	// Resolving Symbol 
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolId,
				namespaceAppl.getConstructor()
				);
		
		assertEquals( 1 , resolvedSymbols.size());
		
		_facade.invalidateGlobalNamespace();
		
		
		resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolId,
				namespaceAppl.getConstructor()
				);
		
		assertEquals( 0 , resolvedSymbols.size());
	}
	
	public void testUnknownConstructorInResolveSymbolShouldThrowException() throws IOException, SpxSymbolTableException {
		
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
		try{
			// Resolving Symbol 
			_facade.resolveSymbols(
				termFactory().makeTuple(
						ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
						symbolId,
						termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0))
				)
		);
		}catch(IllegalArgumentException ex){}
	
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
	 	IStrategoTerm data3 = (IStrategoAppl)moduleDeclarationP1M2.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl3 = typeAppl1;  
		
		
		// Defining Symbol-Table entries  
		IStrategoAppl symbolDef1 = createEntry(namespaceAppl , symbolId1 , typeAppl1  , data1);
		IStrategoAppl symbolDef2 = createEntry(namespaceAppl , symbolId2 , typeAppl2  , data2);
		IStrategoAppl symbolDef3 = createEntry(namespaceAppl , symbolId3 , typeAppl3  , data3);
		
		_facade.indexSymbol(symbolDef1);
		_facade.indexSymbol(symbolDef2);
		_facade.indexSymbol(symbolDef3);
		
		// Resolving Symbols
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols( 
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolId1,
				_facade.getConstructor("SDFDef", 0) 
				);
		
		assertEquals(2 , resolvedSymbols.size());
		
		//Resolving only one Symbol
		resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbol( 
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolId1,
				_facade.getConstructor("SDFDef", 0) 
				);
		
		assertEquals(1 , resolvedSymbols.size());
		
		resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols( 
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolId1,
				_facade.getConstructor("STRDef", 0) 
				);
		
		assertEquals( 1 , resolvedSymbols.size());
		
	}
	
	public void testUnknownSymbolResolveShouldReturnNull() throws IOException, SpxSymbolTableException{
		setupScopeTree();
		
		IStrategoAppl currentAppl = ModuleDeclaration.toModuleQNameAppl(_facade, this.moduleDeclarationP1M2);
		
		IStrategoTerm symbolId1 = termFactory().makeTuple(currentAppl , termFactory().makeString("1")); // defining following composite ID :  (Global() , "TestId") 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(currentAppl , symbolId1 , typeAppl1  , data1));
		
		IStrategoTerm symbolUnknownId = termFactory().makeTuple(currentAppl , termFactory().makeString("_")); // defining following composite ID :  (Global() , "TestId")
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolUnknownId,
				_facade.getConstructor("SDFDef", 0) 
				);
		
		assertEquals(0, resolvedSymbols.size());
	}
	
	public void testResolveModuleSymbols() throws IOException, SpxSymbolTableException{
		setupScopeTree();
		
		IStrategoAppl currentAppl = ModuleDeclaration.toModuleQNameAppl(_facade, this.moduleDeclarationP1M2);
		
		IStrategoTerm symbolId1 = termFactory().makeTuple(currentAppl , termFactory().makeString("1")); // defining following composite ID :  (Global() , "TestId") 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(currentAppl , symbolId1 , typeAppl1  , data1));
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade, moduleDeclarationP1M1),
				symbolId1,
				_facade.getConstructor("SDFDef", 0) 
				);
		
		assertEquals(1, resolvedSymbols.size());
		
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0];
		assertEquals("SDFDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId1, actual.Id(_facade.getTermFactory())));
		assertTrue( SpxSymbol.verifyEquals( currentAppl.getSubterm(0).getSubterm(0) , actual.namespaceUri().id()) );
	}
	
	public void testShouldNotResolveModuleSymbolsFromOtherPackage() throws IOException, SpxSymbolTableException{
		setupScopeTree();
		
		IStrategoAppl currentAppl = ModuleDeclaration.toModuleQNameAppl(_facade, this.moduleDeclarationP1M2);
		
		IStrategoTerm symbolId1 = termFactory().makeTuple(currentAppl , termFactory().makeString("1")); // defining following composite ID :  (Global() , "TestId") 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(currentAppl , symbolId1 , typeAppl1  , data1));
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade, this.moduleDeclarationP2M1),
				symbolId1,
				_facade.getConstructor("SDFDef", 0) 
				);
		
		assertEquals(0, resolvedSymbols.size());
	}
	
	public void testShouldNotResolveModuleInternalSymbolsFromPackage() throws IOException, SpxSymbolTableException{
		setupScopeTree();
		
		IStrategoAppl internalModuleAppl = ModuleDeclaration.toModuleQNameAppl( _facade, 
				PackageNamespace.packageInternalModuleId(
						this.packageDeclaration2.getId(), 
						_facade)
				);
		IStrategoTerm symbolId1 = termFactory().makeTuple(internalModuleAppl , termFactory().makeString("1")); // defining following composite ID :  (Global() , "1") 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(internalModuleAppl , symbolId1 , typeAppl1  , data1));
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				PackageDeclaration.toPackageQNameAppl(_facade, this.packageDeclaration2.getId()),
				symbolId1,
				_facade.getConstructor("SDFDef", 0) 
				);
		
		assertEquals(0, resolvedSymbols.size());
	}
	
	public void testShouldResolveModuleInternalSymbolsFromModule() throws IOException, SpxSymbolTableException{
		setupScopeTree();
		IStrategoAppl internalModuleAppl = ModuleDeclaration.toModuleQNameAppl( _facade, 
				PackageNamespace.packageInternalModuleId(
						this.packageDeclaration2.getId(), 
						_facade)
				);
		IStrategoTerm symbolId1 = termFactory().makeTuple(internalModuleAppl , termFactory().makeString("1")); // defining following composite ID :  (Global() , "1") 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(internalModuleAppl , symbolId1 , typeAppl1  , data1));
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				internalModuleAppl,
				symbolId1,
				_facade.getConstructor("SDFDef", 0) 
				);
		
		assertEquals(1, resolvedSymbols.size());
		
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0];
		assertEquals("SDFDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId1, actual.Id(_facade.getTermFactory())));
		assertTrue( SpxSymbol.verifyEquals( internalModuleAppl.getSubterm(0).getSubterm(0) , actual.namespaceUri().id()) );
	}
	
	public void testShouldNotResolveSymbolFromImportedNamespaceInCyclicReference() throws IOException, SpxSymbolTableException{
		setupScopeTree();
		
		indexImportRef(packageDeclaration1, packageDeclaration1);
		
		IStrategoAppl packageQNameAppl = PackageDeclaration.toPackageQNameAppl(_facade,this.packageDeclaration2.getId());
		
		IStrategoTerm symbolId1 = packageQNameAppl; // defining following packageID
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(packageQNameAppl , symbolId1 , typeAppl1  , data1));
		
		
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				PackageDeclaration.toPackageQNameAppl(_facade,this.packageDeclaration1.getId()),
				symbolId1,
				_facade.getConstructor("ModuleDef", 0) 
				);
		
		assertEquals(0, resolvedSymbols.size());
	}
	
	public void testShouldResolveSymbolFromImportedNamespace() throws IOException, SpxSymbolTableException{
		createExtendedScopeTree();
		
		IStrategoAppl packageQNameAppl = PackageDeclaration.toPackageQNameAppl(_facade,this.packageDeclaration2.getId());
		IStrategoTerm symbolId1 = packageQNameAppl; // defining following packageID
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(packageQNameAppl , symbolId1 , typeAppl1  , data1));
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				PackageDeclaration.toPackageQNameAppl(_facade,this.packageDeclaration1.getId()),
				symbolId1,
				_facade.getConstructor("ModuleDef", 0) 
				);
		
		assertEquals(1, resolvedSymbols.size());
	}
	
	public void testShouldNotAllowTransitiveImports() throws IOException, SpxSymbolTableException{
		createExtendedScopeTree();
		
		String packageName3 =  	"\"lang\", \"p3\"" ;
		
		packageDeclaration3   = indexTestPackageDecl(packageName3, absPathString2);
		moduleDeclarationP3M1 = indexTestModuleDefs ( "p3m1" , packageName3 , absPathString2);
		
		
		IStrategoAppl moduleQnameAppl = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP3M1.getId());
		
		IStrategoTerm symbolId1 = moduleQnameAppl; // defining following packageID
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl , symbolId1 , typeAppl1  , data1));
		
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId()),
				symbolId1,
				_facade.getConstructor("ModuleDef", 0) 
				);
		
		assertEquals(0, resolvedSymbols.size());
		
		resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP2M1.getId()),
				symbolId1,
				_facade.getConstructor("ModuleDef", 0) 
				);
		
		assertEquals(1, resolvedSymbols.size());
		
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0];
		assertEquals("ModuleDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId1, actual.Id(_facade.getTermFactory())));
		assertTrue(SpxSymbol.verifyEquals( this.moduleDeclarationP3M1.getId() , actual.namespaceUri().id()) );
	}
	
	public void testShouldNotRetrunTransitiveImportSymbols() throws IOException, SpxSymbolTableException{
		createExtendedScopeTree();
		
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
		IStrategoAppl moduleQnameAppl3 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP3M1.getId());
		
		// Defining Symbol1 in P1
		IStrategoTerm symbolId1 = moduleQnameAppl1; 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1 , symbolId1 , typeAppl1  , data1));
		
		// Defining Symbol3 in P3
		IStrategoTerm symbolId3 = moduleQnameAppl1; 
	 	IStrategoTerm data3 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl3 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl3 , symbolId3 , typeAppl3 , data3));
		
		
		//Resolving Symbol in Package 1. It will not find any symbol defined in Package2. Hence, 
		//it will return just the symbol from the Package1 .
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId()), // search origin
				symbolId3,	//looking for 
				_facade.getConstructor("ModuleDef", 0) // with type 
				);
		
		
		assertEquals(1, resolvedSymbols.size());
		
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0]; // resolved from the current namespace - Module 1 of Package 1 
		assertEquals("ModuleDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId3, actual.Id(_facade.getTermFactory())));
		assertTrue(SpxSymbol.verifyEquals( this.moduleDeclarationP1M1.getId() , actual.namespaceUri().id()) );
	}
	
	public void testShouldRetrunSymbolsFromImportedScope() throws IOException, SpxSymbolTableException{
		createExtendedScopeTree();
		
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
		IStrategoAppl moduleQnameAppl3 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP3M1.getId());
		
		// Defining Symbol1 in P1
		IStrategoTerm symbolId1 = moduleQnameAppl1; 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1 , symbolId1 , typeAppl1  , data1));
		
		// Defining Symbol3 in P3
		IStrategoTerm symbolId3 = moduleQnameAppl1; 
	 	IStrategoTerm data3 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl3 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl3 , symbolId3 , typeAppl3 , data3));
		
		
		//Resolving Symbol in Package 2 . It should return symbol from imported package3
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP2M1.getId()), // search origin
				symbolId3, // loooking for 
				_facade.getConstructor("ModuleDef", 0) // with type 
				);
		
		assertEquals(1, resolvedSymbols.size());
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0]; // Resolved from the imported namespace - Module 1 of Package 3
		assertEquals("ModuleDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId3, actual.Id(_facade.getTermFactory())));
		assertTrue(SpxSymbol.verifyEquals( this.moduleDeclarationP3M1.getId() , actual.namespaceUri().id()) );
	}
	
	public void testShouldNotFailIncaseOfCyclicImports() throws Exception{
		createExtendedScopeTree();
		
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
		IStrategoAppl moduleQnameAppl3 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP3M1.getId());
		
		// Defining Symbol1 in P1
		IStrategoTerm symbolId1 = moduleQnameAppl1; 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1 , symbolId1 , typeAppl1  , data1));
		
		// Defining Symbol3 in P3
		IStrategoTerm symbolId3 = moduleQnameAppl1; 
	 	IStrategoTerm data3 = (IStrategoAppl)moduleDeclarationP3M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl3 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl3 , symbolId3 , typeAppl3 , data3));
		
		_facade.persistChanges();
		_registry.closePersistenceManager(this.projectNameTerm);
		
		_registry.initFacade(projectNameTerm, termFactory(), ioAgent());
		SpxSemanticIndexFacade tfacade = _registry.getFacade(this.projectNameTerm);
		
		//Resolving Symbol in Package 3
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)tfacade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP3M1.getId()), // search origin
				symbolId3, // loooking for 
				tfacade.getConstructor("ModuleDef", 0) // with type 
				);
		
		// current import chain is as following : p1-> p2 ->p3->p1
		// symbol 1 is defined in P1 
		// symbol 3 is defined in P3
		// Symbol 1 Id and Type = Symbol 3 ID and Type
		// Hence, while resolving for symbol1/symbol3 in Package3 , it will return following :
		// Symbol3  -> due to the fact that it is enclosed symbol of the current namespace
		// Symbol1  -> due to the import reference from P3 -> P 1
		assertEquals(2, resolvedSymbols.size());
		
		//Resolving Symbol in Package 3. It will just resolve one symbol and exit search
		resolvedSymbols = (Set<SpxSymbol>)tfacade.resolveSymbol(
				ModuleDeclaration.toModuleQNameAppl(tfacade,this.moduleDeclarationP3M1.getId()), // search origin
				symbolId3, // loooking for 
				tfacade.getConstructor("ModuleDef", 0) // with type 
				);
		
		assertEquals(1, resolvedSymbols.size());
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0]; // Resolved from the imported namespace - Module 1 of Package 3
		assertTrue(SpxSymbol.verifyEquals( this.moduleDeclarationP3M1.getId() , actual.namespaceUri().id()) );
	}
	
	public void testAnonymousScopeCreation() throws IOException, SpxSymbolTableException {
		createExtendedScopeTree();
		
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
	
		IStrategoAppl nsAppl = (IStrategoAppl)_facade.insertNewScope(moduleQnameAppl1);
		
		assertEquals(_facade.getLocalNamespaceTypeCon(), nsAppl.getConstructor());
		
		INamespace ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace((IStrategoList) nsAppl.getSubterm(0));
		
		assertNotNull(ns);
		
	}
	
	public void testDestroyAnonymousScope() throws IOException, SpxSymbolTableException {
		createExtendedScopeTree();
		
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
	
		IStrategoAppl nsAppl = (IStrategoAppl)_facade.insertNewScope(moduleQnameAppl1);
		
		assertEquals(_facade.getLocalNamespaceTypeCon(), nsAppl.getConstructor());
		
		INamespace ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace((IStrategoList) nsAppl.getSubterm(0));
		assertNotNull(ns);
		
		try{
			nsAppl = (IStrategoAppl)_facade.destroyScope(moduleQnameAppl1);
		}
		catch(IllegalArgumentException ex){}
		
		nsAppl = (IStrategoAppl)_facade.destroyScope(nsAppl);
		assertNotNull(ns);
		
		ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace((IStrategoList) nsAppl.getSubterm(0));
		assertNull(ns);
	}
	
	public void testResolveInAnonymousScopeShouldReturnEnclosingSymbols() throws IOException, SpxSymbolTableException {
		createExtendedScopeTree();
		
		//adding a symbol in moduleDeclarationP1M1
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
		IStrategoTerm symbolId1 = moduleQnameAppl1; 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1 , symbolId1 , typeAppl1  , data1));
		
		//Creating anonymous scope
		IStrategoAppl nsAppl = (IStrategoAppl)_facade.insertNewScope(moduleQnameAppl1);
		assertEquals(_facade.getLocalNamespaceTypeCon(), nsAppl.getConstructor());
		INamespace ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace((IStrategoList) nsAppl.getSubterm(0));
		assertNotNull(ns);
		
		//Resolving Symbol from ModuleP1M1. It will not find any symbol defined in localScope. Hence, 
		//it will return just the symbol from the ModuleP1M1 .
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				nsAppl, // search origin
				symbolId1,	//looking for 
				_facade.getConstructor("ModuleDef", 0) // with type 
				);
		
		
		assertEquals(1, resolvedSymbols.size());
		
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0];// resolved from the current namespace - Module 1 of Package 1 
		assertEquals("ModuleDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId1, actual.Id(_facade.getTermFactory())));
		assertTrue(SpxSymbol.verifyEquals( this.moduleDeclarationP1M1.getId() , actual.namespaceUri().id()) );
	
		//Resolve symbol defined in GlobalScope 
		IStrategoAppl globalNamespaceAppl = termFactory().makeAppl(_facade.getGlobalNamespaceTypeCon());
		
		IStrategoTerm symbolId2 = termFactory().makeTuple( globalNamespaceAppl , termFactory().makeString("1")); // defining following composite ID :  (Global() , "TestId") 
	 	IStrategoTerm data2 = (IStrategoAppl)moduleDeclarationP1M2.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl2 = termFactory().makeAppl(termFactory().makeConstructor("SDFDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(globalNamespaceAppl , symbolId2 , typeAppl2  , data2));
		
		//Resolving Symbol defined in Global Namespace. 		
		resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				nsAppl, // search origin
				symbolId2,	//looking for 
				_facade.getConstructor("SDFDef", 0) // with type 
				);
		
		
		assertEquals(1, resolvedSymbols.size());
		
		actual = (SpxSymbol)resolvedSymbols.toArray()[0]; // resolved from the current namespace - Module 1 of Package 1 
		assertEquals("SDFDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId2, actual.Id(_facade.getTermFactory())));
		assertTrue(SpxSymbol.verifyEquals( GlobalNamespace.getGlobalNamespaceId(_facade) , actual.namespaceUri().id()) );
	
	}
	
	

	public void testResolveInAnonymousScope() throws IOException, SpxSymbolTableException {
		createExtendedScopeTree();
		
		IStrategoAppl globalNsAppl = termFactory().makeAppl(_facade.getGlobalNamespaceTypeCon());
		
		//adding a symbol in moduleDeclarationP1M1
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
		IStrategoTerm symbolId1 = moduleQnameAppl1; 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		//Creating anonymous scope
		IStrategoAppl nsAppl = (IStrategoAppl)_facade.insertNewScope(globalNsAppl);
		assertEquals(_facade.getLocalNamespaceTypeCon(), nsAppl.getConstructor());
		INamespace ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace((IStrategoList) nsAppl.getSubterm(0));
		assertNotNull(ns);
		
		_facade.indexSymbol(createEntry(nsAppl , symbolId1 , typeAppl1  , data1));
		
		//Resolving Symbol from ModuleP1M1. It will not find any symbol defined in localScope. Hence, 
		//it will return just the symbol from the ModuleP1M1 .
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)_facade.resolveSymbols(
				nsAppl, // search origin
				symbolId1,	//looking for 
				_facade.getConstructor("ModuleDef", 0) // with type 
				);
		
		
		assertEquals(1, resolvedSymbols.size());
		
		SpxSymbol actual = (SpxSymbol)resolvedSymbols.toArray()[0];// resolved from the current local namespace
		assertEquals("ModuleDef", actual.type());
		assertTrue(SpxSymbol.verifyEquals(symbolId1, actual.Id(_facade.getTermFactory())));
		assertTrue(SpxSymbol.verifyEquals( (IStrategoList)nsAppl.getSubterm(0) , actual.namespaceUri().id()) );
		
		_facade.destroyScope(nsAppl);
		ns = _facade.persistenceManager().spxSymbolTable().resolveNamespace((IStrategoList) nsAppl.getSubterm(0));
		assertNull(ns);
	}
	
	public void testUndefineSymbols() throws Exception{
		createExtendedScopeTree();
	
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
		
		// Defining Symbol1 in P1.M1
		IStrategoTerm symbolId1 = moduleQnameAppl1; 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1 , symbolId1 , typeAppl1  , data1));
		
		// Defining Symbol1 in P1.M2
		IStrategoTerm symbolId2 = moduleQnameAppl1; 
	 	IStrategoTerm data2 = (IStrategoAppl)moduleDeclarationP1M2.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl2 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef__", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1, symbolId2 , typeAppl2  , data2));
		
		// Defining Symbol3 in P2.M1
		IStrategoTerm symbolId3 = moduleQnameAppl1; 
	 	IStrategoTerm data3 = (IStrategoAppl)moduleDeclarationP2M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl3 = typeAppl1;
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1 , symbolId3 , typeAppl3 , data3));
		
		
		_facade.persistChanges();
		
		// closing persistence manager
		_registry.closePersistenceManager(this.projectNameTerm);
		_registry.initFacade(projectNameTerm, termFactory(), ioAgent());
		
		// loading tfacade again
		SpxSemanticIndexFacade tfacade = _registry.getFacade(this.projectNameTerm);
		
		Set<SpxSymbol> resolvedSymbols = tfacade
				.persistenceManager()
				.spxSymbolTable()
				.undefineSymbols(this.moduleDeclarationP1M1.getId(), symbolId1,
						typeAppl1.getConstructor());
		
		assertEquals(2, resolvedSymbols.size());
		
		for( SpxSymbol sym : resolvedSymbols) {
			assertTrue(sym.equalType(typeAppl1.getConstructor())); 
		}
		
		List<SpxSymbol> resolvedSymbols1 = tfacade.persistenceManager().spxSymbolTable()
				.resolveNamespace(this.moduleDeclarationP1M1.getId())
				.getMembers()
				.get(new SpxSymbolKey(symbolId1));
		
		assertEquals(1, resolvedSymbols1.size());
		
		for( SpxSymbol sym : resolvedSymbols1) {
			assertTrue(sym.equalType(typeAppl2.getConstructor())); 
		}
	}
	
	public void testResolveShouldReturnAllSymbolsOfaType() throws Exception{
		createExtendedScopeTree();
		
		IStrategoAppl moduleQnameAppl1 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId());
		IStrategoAppl moduleQnameAppl2 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M2.getId());
		IStrategoAppl moduleQnameAppl3 = ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP2M1.getId());
		
		// Defining Symbol1 in P1.M1
		IStrategoTerm symbolId1 = moduleQnameAppl1; 
	 	IStrategoTerm data1 = (IStrategoAppl)moduleDeclarationP1M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl1 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl1 , symbolId1 , typeAppl1  , data1));
		
		// Defining Symbol1 in P1.M2
		IStrategoTerm symbolId2 = moduleQnameAppl1; 
	 	IStrategoTerm data2 = (IStrategoAppl)moduleDeclarationP1M2.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl2 = termFactory().makeAppl(termFactory().makeConstructor("ModuleDef__", 0)); // setting Type  
		
		_facade.indexSymbol(createEntry(moduleQnameAppl2 , symbolId2 , typeAppl2  , data2));
		
		// Defining Symbol3 in P2.M1
		IStrategoTerm symbolId3 = moduleQnameAppl1; 
	 	IStrategoTerm data3 = (IStrategoAppl)moduleDeclarationP2M1.toTerm(_facade);	// defining Data
		IStrategoAppl typeAppl3 = typeAppl1;
		
		_facade.indexSymbol(createEntry(moduleQnameAppl3 , symbolId3 , typeAppl3 , data3));
		
		_facade.persistChanges();
		
		// closing persistence manager
		_registry.closePersistenceManager(this.projectNameTerm);
		_registry.initFacade(projectNameTerm, termFactory(), ioAgent());
		
		// loading tfacade again
		SpxSemanticIndexFacade tfacade = _registry.getFacade(this.projectNameTerm);
		

		//-------------------------Resolving Symbol in Package 3--------------------------------------------
		// It should only return 2 symbols defined in P1.M1         									   |
		//--------------------------------------------------------------------------------------------------
		Set<SpxSymbol> resolvedSymbols = (Set<SpxSymbol>)tfacade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP3M1.getId()), // search origin
				termFactory().makeString(Utils.All), // loooking for * symbols 
				typeAppl1.getConstructor() // of Type 
				);
		
		assertEquals(1, resolvedSymbols.size());
		
		for( SpxSymbol sym : resolvedSymbols) {
			assertTrue(sym.equalType(typeAppl1.getConstructor())); 
		}
		
		//-------------------------Resolving Symbol in Package 1--------------------------------------------
		// It should only return 2 symbols defined in P1.M1 and P2.M1                                      |
		//--------------------------------------------------------------------------------------------------
		resolvedSymbols = (Set<SpxSymbol>)tfacade.resolveSymbols(
				ModuleDeclaration.toModuleQNameAppl(_facade,this.moduleDeclarationP1M1.getId()), // search origin
				termFactory().makeString(Utils.All), // loooking for * symbols 
				typeAppl1.getConstructor() // of Type 
				);
		
		assertEquals(2, resolvedSymbols.size());
		
		for( SpxSymbol sym : resolvedSymbols) {
			assertTrue(sym.equalType(typeAppl1.getConstructor())); 
		}
	}
	
	private void indexImportRef ( PackageDeclaration decl ,  PackageDeclaration importPackageDecl) throws SpxSymbolTableException{
		IStrategoConstructor importCtor  = _facade.getImportDeclCon();
		IStrategoAppl importDecl = this.termFactory().makeAppl(importCtor,  
				PackageDeclaration.toPackageQNameAppl(_facade,decl.getId()),
				termFactory().makeList(
						PackageDeclaration.toPackageQNameAppl(_facade,importPackageDecl.getId())
				)
		);
		
		_facade.indexImportReferences(importDecl);
		
	}
	private void createExtendedScopeTree() throws IOException, SpxSymbolTableException{
		// Setting up a big Scope-Tree
		setupScopeTree();
		
		String packageName3 =  	"\"lang\", \"p3\"" ;
		
		packageDeclaration3   = indexTestPackageDecl(packageName3, absPathString2);
		moduleDeclarationP3M1 = indexTestModuleDefs ( "p3m1" , packageName3 , absPathString2);
		
		//Setting up following import hierarchy : P1 -> P2 -> P3 -> p1
		indexImportRef( packageDeclaration1 , packageDeclaration2);
		indexImportRef( packageDeclaration2 , packageDeclaration3);
		indexImportRef( packageDeclaration3 , packageDeclaration1);
		
		// ------ScopeTree setup is done :  P1 imports P2 import P3 imports P1--------
		// 
		//                         Global 
		//                           |
		//							 | 
		//         p1--------->------p2------------>-------------P3
		//          |                |                            |     
		//  -----------------       --------------            -----------  
		//   |      |       |       |            |            |         |  
		//  p1M1   p1M2 _internal   _internal    p2M1       _internal   P3M1
		//
		//-----------------------------------------------------------------------------
		
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
	
	private ModuleDeclaration indexTestModuleDefs( String moduleName , String packageQName , String filePath) throws SpxSymbolTableException, IOException
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
