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
		_facade.clearSymbolTable();
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
		
		_facade.assertConstructor(packageDeclaration.getConstructor(), _facade.getPackageDeclCon(), "Wrong Package Declaration"); 
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
		
		_facade.assertConstructor(packageDeclaration.getConstructor(), _facade.getPackageDeclCon(), "Wrong Package Declaration");
		
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
		
		_facade.assertConstructor(moduleDeclaration.getConstructor(), _facade.getModuleDeclCon(), "Wrong Module Declaration Constructs");
	}
	
}
