package org.spoofax.interpreter.library.language.tests;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.test.AbstractInterpreterTest;

public class SpxSemanticIndexFacadeTest extends AbstractInterpreterTest{
	
	private final String _projectName = "test";
	
	private IStrategoString projectNameTerm; 
	private SpxSemanticIndexFacade _facade;
	
	final String absPathString1 = "c:/temp/test1.spx" ;
	final String absPathString2 = "c:/temp/test2.spx" ;
	
	private Interpreter interpreter()
	{
		 return itp;
	}
	
	private ITermFactory termFactory() {
		return factory;
	}

	private IOAgent ioAgent() {
		return itp.getIOAgent(); 
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp("C:/work/projects/spoofax/spx-imp/source-codes/trunk/org.strategoxt.imp.editors.spoofax/include");
		interpreter().addOperatorRegistry(new LanguageLibrary());
		
		projectNameTerm = termFactory().makeString(_projectName);
		
	
		_facade = new SpxSemanticIndexFacade(projectNameTerm , termFactory() , ioAgent());
		_facade.reinitSymbolTable();
	}
	
	@Override 
	protected void tearDown() throws Exception {
		_facade.close();
	}
	
	public void testListenerRemovingRecordsFromChildSymbolTables()
	{
		
	}	
	
	public void testUnknownPackageNameThrowsIllegalArgumentException() 
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
	
	public void testIndexPackageDeclaration() 
	{
		ITermFactory f = termFactory() ;

		String moduleName =  "m1" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)f.parseFromString("Package(QName([\"languages\", \"entitylang\"]))"); 
		IStrategoAppl mQnameAppl = (IStrategoAppl)f.parseFromString("Module(QName([\"languages\", \"entitylang\" , \""+ moduleName  +"\"]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(f, moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(f, moduleName);
		
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString1));
		
		IStrategoAppl packageDeclaration = (IStrategoAppl)_facade.getPackageDeclaration(pQnameAppl);
		
		_facade.verifyConstructor(packageDeclaration.getConstructor(), _facade.getPackageDeclCon(), "Wrong Package Declaration"); 
	}
	
	public void testIndexPackageDeclarationInMultipleFiles() 
	{
		ITermFactory f = termFactory() ;

		String moduleName =  "m1" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)f.parseFromString("Package(QName([\"languages\", \"entitylang\"]))"); 
		IStrategoAppl mQnameAppl = (IStrategoAppl)f.parseFromString("Module(QName([\"languages\", \"entitylang\" , \""+ moduleName  +"\"]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(f, moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(f, moduleName);
		
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString1));
		_facade.indexPackageDeclaration(pQnameAppl, f.makeString(absPathString2));
		
		IStrategoAppl packageDeclaration = (IStrategoAppl)_facade.getPackageDeclaration(pQnameAppl);
		
		_facade.verifyConstructor(packageDeclaration.getConstructor(), _facade.getPackageDeclCon(), "Wrong Package Declaration");
		
		assertEquals(2, ((IStrategoList)packageDeclaration.getSubterm(1)).getAllSubterms().length);
	}
	
	public void testIndexModuleDeclaration() 
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
		
		_facade.verifyConstructor(moduleDeclaration.getConstructor(), _facade.getModuleDeclCon(), "Wrong Module Declaration Constructs");
	}
	
	private void indexTestModuleDefs( String moduleName , String packageQName , String filePath)
	{
		String moduleQName = packageQName  + ", \""+ moduleName  +"\"" ;
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageQName+"]))");
		
		IStrategoAppl mQnameAppl = (IStrategoAppl)termFactory().parseFromString("Module(QName(["+moduleQName+ "]))");
		IStrategoAppl ast = (IStrategoAppl)SpxLookupTableUnitTests.getModuleDefinition(termFactory(), moduleName);
		IStrategoAppl analyzed_ast = (IStrategoAppl)SpxLookupTableUnitTests.getAnalyzedModuleDefinition(termFactory(), moduleName);
		
		_facade.indexModuleDefinition(mQnameAppl, termFactory().makeString(filePath), pQnameAppl, ast, analyzed_ast);
	}
	
	private IStrategoAppl indexTestPackageDecl(String packageName , String fileName) {
		
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageName+"]))");
		_facade.indexPackageDeclaration(pQnameAppl, termFactory().makeString(fileName));
		return pQnameAppl;
	}
	
	public void testGetModuleDeclarationsByPackageId()
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

	public void testGetModuleDeclarationsWithUnknownPackageID()
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
	
	public void testGetModuleDeclarationsByFileUri()
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
	}
	
	
	public void testGetPackageDeclarationsByUri()
	{
		String packageName1 =  	"\"languages\", \"entitylang\"" ;

		indexTestPackageDecl(packageName1, absPathString1);
		
		String packageName2 =  	"\"languages\", \"entitylang2\"" ;
		indexTestPackageDecl(packageName2, absPathString1);
		
		String packageName3 =  	"\"languages\", \"entitylang2\"" ;
		indexTestPackageDecl(packageName3, absPathString2);
		
		
		IStrategoList actuals = null;

		
		actuals = _facade.getPackageDeclarationsByUri(termFactory().makeString(absPathString2));
		assertEquals(1, actuals.getSubtermCount());
		

		actuals = _facade.getPackageDeclarationsByUri(termFactory().makeString(absPathString1));
		assertEquals(2, actuals.getSubtermCount());
	}

}
