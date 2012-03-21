package org.spoofax.interpreter.library.language.spxlang.index.tests;

import java.io.IOException;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacadeRegistry;
import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class SpxSemanticIndexFacadeTest extends SpxIndexBaseTestCase{
	
	private final String _projectName = ".UnitTest2";
	
	private SpxSemanticIndexFacade _facade;
	private SpxSemanticIndexFacadeRegistry _registry;
	
	final String absPathString1 = "c:/temp/test1.spx" ;
	final String absPathString2 = "c:/temp/test2.spx" ;

	private Interpreter interpreter(){ return itp;	}

	private IOAgent ioAgent() { return itp.getIOAgent(); }
	
	private ITermFactory termFactory() { return factory; 	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		interpreter().addOperatorRegistry(new LanguageLibrary());
		_registry = new SpxSemanticIndexFacadeRegistry();
		
		_registry.initFacade(getProjectPath(_projectName), termFactory(), ioAgent()); 
		_facade = _registry.getFacade(getProjectPath(_projectName));
		_facade.cleanIndexAndSymbolTable();
	
		indexCompilationUnit();
	}
	
	@Override 
	protected void tearDown() throws Exception { _facade.close(false); }
	
	public void testGetModuleDeclarationsByFileUri() throws IOException, SpxSymbolTableException
	{
		String packageName1 =  	"\"languages\", \"entitylang\"" ;

		indexTestPackageDecl(packageName1, absPathString1);
		indexTestModuleDefs ( "p1m1" , packageName1 , absPathString1);
		indexTestModuleDefs ( "p1m2" , packageName1 , absPathString1);
		
		String packageName2 =  	"\"languages\", \"entitylang2\"" ;
		indexTestPackageDecl(packageName2, absPathString1);
		indexTestModuleDefs ( "p2m2" , packageName2 , absPathString1);
		
		String packageName3 =  	"\"languages\", \"entitylang2\"" ;
		indexTestPackageDecl(packageName3, absPathString2);
		indexTestModuleDefs ( "p3m2" , packageName3 , absPathString2);
		
		IStrategoList actuals = null;
		//following invocation should return 2 ModuleDeclarations
		actuals = _facade.getModuleDeclarations( termFactory().makeString(absPathString1));
		
		assertEquals(3, actuals.getSubtermCount());
		
		//following invocation should return 1  ModuleDeclarations
		actuals = _facade.getModuleDeclarations( termFactory().makeString(absPathString2));
		assertEquals(1, actuals.getSubtermCount());
		
		
		//Test Namespaces 
		_facade.getPersistenceManager().spxSymbolTable().getAllNamespaceSpxId();
	}
	
	public void testGetModuleDeclarationsByPackageId() throws SpxSymbolTableException, IOException
	{
		String packageName1 =  	"\"languages\", \"entitylang\"" ;
		
		IStrategoAppl pQnameAppl = indexTestPackageDecl(packageName1, absPathString1);
		indexTestModuleDefs ( "m1" , packageName1 , absPathString1);
		indexTestModuleDefs ( "m2" , packageName1 , absPathString1);
		
		String packageName2 =  	"\"languages\", \"entitylang2\"" ;
		IStrategoAppl pQnameAppl2 = indexTestPackageDecl(packageName2, absPathString1);
		indexTestModuleDefs ( "m2" , packageName2 , absPathString1);
		
		IStrategoList actuals = null;
		
		//following invocation should return 2 ModuleDeclarations
		actuals = _facade.getModuleDeclarations( pQnameAppl );
		
		assertEquals(2, actuals.getSubtermCount());
		
		//following invocation should return 1 ModuleDeclarations
		actuals = _facade.getModuleDeclarations( pQnameAppl2 );
		
		assertEquals(1, actuals.getSubtermCount());
	}

	public void testGetModuleDeclarationsWithUnknownPackageID() throws SpxSymbolTableException, IOException
	{
		String packageName1 =  	"\"languages\", \"entitylang\"" ;
		indexTestPackageDecl(packageName1, absPathString1);
		indexTestModuleDefs ( "m1" , packageName1 , absPathString1);
		indexTestModuleDefs ( "m2" , packageName1 , absPathString1);
		
		String packageName2 =  	"\"languages\", \"entitylang2\"" ;
		indexTestPackageDecl(packageName2, absPathString1);
		indexTestModuleDefs ( "m2" , packageName2 , absPathString1);
		
		
		String unknown =  	"\"languages\", \"unknown\"" ;
		IStrategoAppl pUnknownQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+unknown+"]))");
		IStrategoList actuals = null;
		
		try{
			//following invocation should return 2 ModuleDeclarations
			actuals = _facade.getModuleDeclarations( pUnknownQnameAppl );
		}catch(IllegalArgumentException ex)
		{
			
		}
		
	}

	public void testGetPackageDeclarationsByUri() throws IOException
	{
		String packageName1 =  	"\"languages\", \"entitylang\"" ;

		indexTestPackageDecl(packageName1, absPathString1);
		
		String packageName2 =  	"\"languages\", \"entitylang2\"" ;
		indexTestPackageDecl(packageName2, absPathString1);
		
		String packageName3 =  	"\"languages\", \"entitylang2\"" ;
		indexTestPackageDecl(packageName3, absPathString2);
		
		IStrategoList actuals = null;
		
		actuals = _facade.getPackageDeclarations(termFactory().makeString(absPathString2));
		assertEquals(1, actuals.getSubtermCount());

		actuals = _facade.getPackageDeclarations(termFactory().makeString(absPathString1));
		assertEquals(2, actuals.getSubtermCount());
		
		
	}
	
	public void testIndexingImportReferences() throws SpxSymbolTableException, IOException
	{

		String packageName1 =  	"\"languages\", \"entitylang1\"" ;
		String packageName2 =  	"\"languages\", \"entitylang2\"" ;
		
		IStrategoAppl pQnameAppl1 = indexTestPackageDecl(packageName1, absPathString1);
		IStrategoAppl pQnameAppl2 = indexTestPackageDecl(packageName2, absPathString1);
		
		String packageName3 =  	"\"languages\", \"entitylang3\"" ;
		IStrategoAppl pQnameAppl3 = indexTestPackageDecl(packageName3, absPathString1);
		IStrategoAppl mQnameAppl2  = indexTestModuleDefs ( "m2" , packageName3 , absPathString1);
		
		IStrategoAppl importDecl1 = this.termFactory().makeAppl(_facade.getCons().getImportDeclCon(), pQnameAppl3,  this.termFactory().makeList(pQnameAppl1) );
		this._facade.indexImportReferences(importDecl1);
		
		IStrategoAppl importDecl2 = this.termFactory().makeAppl(_facade.getCons().getImportDeclCon(), mQnameAppl2,  this.termFactory().makeList(pQnameAppl2) );
		this._facade.indexImportReferences(importDecl2);
		
		
		IStrategoList actuals = null;
		
		actuals = (IStrategoList) _facade.getImportReferences(pQnameAppl1);
		
		assertEquals(0, actuals.size());
		

		actuals = (IStrategoList) _facade.getImportReferences( mQnameAppl2 );
		assertEquals(2, actuals.size());
		
		//following invocation should return both the import reference of itself and enclosing
		//modules' import references. 
		//Hence, it will return both pQnameAppl1 and pQnameAppl2
		actuals = (IStrategoList) _facade.getImportReferences( pQnameAppl3 );
		
		assertEquals(2, actuals.size());

		PackageDeclaration decl =  _facade.lookupPackageDecl(pQnameAppl1);
		
		assertEquals(1, _facade.getPersistenceManager().spxPackageTable().getImportedToReferencesOf(decl.getId()).size());
	}
	
	
	public void testIndexingImportToReferences() throws SpxSymbolTableException, IOException
	{

		String packageName1 =  	"\"languages\", \"entitylang1\"" ;
		String packageName2 =  	"\"languages\", \"entitylang2\"" ;
		
		IStrategoAppl pQnameAppl1 = indexTestPackageDecl(packageName1, absPathString1);
		IStrategoAppl pQnameAppl2 = indexTestPackageDecl(packageName2, absPathString1);
		
		String packageName3 =  	"\"languages\", \"entitylang3\"" ;
		IStrategoAppl pQnameAppl3 = indexTestPackageDecl(packageName3, absPathString1);
		
		
		
		// p3 imports p1
		IStrategoAppl importDecl1 = this.termFactory().makeAppl(_facade.getCons().getImportDeclCon(), pQnameAppl3,  
																this.termFactory().makeList(pQnameAppl1) );
		this._facade.indexImportReferences(importDecl1);
		
		
		//p3 imports p2
		importDecl1 = this.termFactory().makeAppl(_facade.getCons().getImportDeclCon(), pQnameAppl3,  
				this.termFactory().makeList(pQnameAppl2) );
		this._facade.indexImportReferences(importDecl1);

			
		IStrategoList actuals = null;
		
		actuals = (IStrategoList) _facade.getImportReferences(pQnameAppl1);
		assertEquals(0, actuals.size());
		
		
		//following invocation should return both the import reference of itself and enclosing
		//modules' import references. 
		//Hence, it will return both pQnameAppl1 and pQnameAppl2
		actuals = (IStrategoList) _facade.getImportReferences( pQnameAppl3 );
		assertEquals(2, actuals.size());

		PackageDeclaration decl =  _facade.lookupPackageDecl(pQnameAppl1);
		
		assertEquals(1, _facade.getPersistenceManager().spxPackageTable().getImportedToReferencesOf(decl.getId()).size());
		
		
		_facade.removePackageDeclaration(this.termFactory().makeString(absPathString1), pQnameAppl1);
		indexTestPackageDecl(packageName1, absPathString1);
		decl =  _facade.lookupPackageDecl(pQnameAppl1);
		
		
		assertEquals(1, _facade.getPersistenceManager().spxPackageTable().getImportedToReferencesOf(decl.getId()).size());
	}
	
	
	public void testIndexModuleDeclaration() throws IllegalArgumentException, SpxSymbolTableException, IOException 
	{
		ITermFactory f = termFactory() ;

		String moduleName =  "m1" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)f.parseFromString("Package(QName([\"languages\", \"entitylang\"]))"); 
		
		IStrategoAppl mQnameAppl = (IStrategoAppl)f.parseFromString("Module(QName([\"languages\", \"entitylang\" , \""+ moduleName  +"\"]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(f, moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(f, moduleName);
		
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString1));
		_facade.indexModuleDefinition(mQnameAppl, f.makeString(absPathString1), pQnameAppl, ast, analyzed_ast);
		
		
		IStrategoAppl moduleDeclaration = (IStrategoAppl)_facade.getModuleDeclaration( mQnameAppl );
		
		_facade.verifyConstructor(moduleDeclaration.getConstructor(), _facade.getCons().getModuleDeclCon(), "Wrong Module Declaration Constructs");
	}	
	
	public void testIndexPackageDeclaration() throws SpxSymbolTableException 
	{
		ITermFactory f = termFactory() ;

		String moduleName =  "m1" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)f.parseFromString("Package(QName([\"languages\", \"entitylang\"]))"); 
		IStrategoAppl mQnameAppl = (IStrategoAppl)f.parseFromString("Module(QName([\"languages\", \"entitylang\" , \""+ moduleName  +"\"]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(f, moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(f, moduleName);
		
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString1));
		
		IStrategoAppl packageDeclaration = (IStrategoAppl)_facade.getPackageDeclaration(pQnameAppl);
		
		_facade.verifyConstructor(packageDeclaration.getConstructor(), _facade.getCons().getPackageDeclCon(), "Wrong Package Declaration"); 
	}
	
	public void testIndexPackageDeclarationInMultipleFiles() throws SpxSymbolTableException {
		ITermFactory f = termFactory() ;
		
		String moduleName =  "m1" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)f.parseFromString("Package(QName([\"languages\", \"entitylang\"]))"); 
		IStrategoAppl mQnameAppl = (IStrategoAppl)f.parseFromString("Module(QName([\"languages\", \"entitylang\" , \""+ moduleName  +"\"]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(f, moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(f, moduleName);
		
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString1));
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString2));
		
		IStrategoAppl packageDeclaration = (IStrategoAppl)_facade.getPackageDeclaration(pQnameAppl);
		
		_facade.verifyConstructor(packageDeclaration.getConstructor(), _facade.getCons().getPackageDeclCon(), "Wrong Package Declaration");
	
		assertEquals(2, ((IStrategoList)packageDeclaration.getSubterm(1)).getAllSubterms().length);
	}
	
	public void testIndexremovePackageDeclaredinMultipleFiles() throws SpxSymbolTableException, IOException 
	{
		ITermFactory f = termFactory() ;
		
		String moduleName =  "m1" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)f.parseFromString("Package(QName([\"languages\", \"entitylang\"]))");
		
		IStrategoAppl mQnameAppl = (IStrategoAppl)f.parseFromString("Module(QName([\"languages\", \"entitylang\" , \""+ moduleName  +"\"]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(f, moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(f, moduleName);
		
		//setting up index
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString1));
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString2));
		_facade.indexModuleDefinition(mQnameAppl, f.makeString(absPathString2), pQnameAppl, ast, analyzed_ast);
		
		
		//getting package declaration
		IStrategoAppl packageDeclaration = (IStrategoAppl)_facade.getPackageDeclaration(pQnameAppl);
		
		_facade.verifyConstructor(packageDeclaration.getConstructor(), _facade.getCons().getPackageDeclCon(), "Wrong Package Declaration");
		
		assertEquals(2, ((IStrategoList)packageDeclaration.getSubterm(1)).getAllSubterms().length);
		
		//removing uri from the declaration
		_facade.removePackageDeclaration( f.makeString(absPathString1), pQnameAppl);
		packageDeclaration = (IStrategoAppl)_facade.getPackageDeclaration(pQnameAppl);
		assertEquals(1, ((IStrategoList)packageDeclaration.getSubterm(1)).getAllSubterms().length);
		
		// removing uri from the declaration . This time there will not be any uri left. Hence
		// the package will be deleted as well as it all enlosed modules.
		_facade.removePackageDeclaration( f.makeString(absPathString2), pQnameAppl);
		
		try {
			_facade.getPackageDeclaration(pQnameAppl);
		} catch (SpxSymbolTableException ex) { // hence , not found in the
												// symbol table

		}

		try {
			_facade.getModuleDeclaration(mQnameAppl);
		} catch (SpxSymbolTableException ex) { // hence , not found in the
												// symbol table
		}

	}
	
	public void indexCompilationUnit() throws IOException
	{	
		ITermFactory f = termFactory() ;
		
		_facade.indexCompilationUnit(f.makeString(absPathString1),
				(IStrategoAppl) getCompilationUnit(f));
		
		_facade.indexCompilationUnit(f.makeString(absPathString2),
				(IStrategoAppl) getCompilationUnit(f));
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
	
	public void testUnknownPackageNameThrowsIllegalArgumentException() throws IOException 
	{
		ITermFactory f = termFactory() ;

		String moduleName =  "m1" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)f.parseFromString("Package(QName([\"languages\", \"entitylang\"]))"); 
		IStrategoAppl mQnameAppl = (IStrategoAppl)f.parseFromString("Module(QName([\"languages\", \"entitylang\" , \""+ moduleName  +"\"]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(f, moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(f, moduleName);
		
		try{
		_facade.indexModuleDefinition( mQnameAppl ,f.makeString(absPathString1) ,pQnameAppl , ast, analyzed_ast);
		}
		catch(IllegalArgumentException ex)
		{
			
		}
	}
	
	private IStrategoAppl indexTestModuleDefs( String moduleName , String packageQName , String filePath) throws IOException
	{
		String moduleQName = packageQName  + ", \""+ moduleName  +"\"" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageQName+"]))");
		
		IStrategoAppl mQnameAppl = (IStrategoAppl)termFactory().parseFromString("Module(QName(["+moduleQName+ "]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(termFactory(), moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(termFactory(), moduleName);
		
		_facade.indexModuleDefinition(mQnameAppl, termFactory().makeString(filePath), pQnameAppl, ast, analyzed_ast);
		
		return mQnameAppl;
	}
	
	private IStrategoAppl indexTestPackageDecl(String packageName , String fileName) {
		
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageName+"]))");
		_facade.indexPackageDeclaration(pQnameAppl, termFactory().makeString(fileName));
		return pQnameAppl;
	}
}
