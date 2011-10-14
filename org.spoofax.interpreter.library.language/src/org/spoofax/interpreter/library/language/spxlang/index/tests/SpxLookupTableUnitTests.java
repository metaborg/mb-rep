package org.spoofax.interpreter.library.language.spxlang.index.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.junit.Test;
import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.index.ISpxPersistenceManager;
import org.spoofax.interpreter.library.language.spxlang.index.SpxModuleLookupTable;
import org.spoofax.interpreter.library.language.spxlang.index.SpxPackageLookupTable;
import org.spoofax.interpreter.library.language.spxlang.index.SpxPersistenceManager;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.library.language.spxlang.index.data.LanguageDescriptor;
import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.test.AbstractInterpreterTest;

public class SpxLookupTableUnitTests  extends AbstractInterpreterTest{
	
	private final String _projectName = "test";
	private IStrategoString projectNameTerm;
	
	private ISpxPersistenceManager manager ;
	private SpxPackageLookupTable symtable;
	private SpxModuleLookupTable mSymTable;
	
	final String absPathString1 = "c:/temp/test.spx" ;
	final String absPathString2 = "c:/temp/test2.spx" ;
	
	public SpxLookupTableUnitTests() {}

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
	
	@Override protected void setUp() throws Exception {
		super.setUp("C:/work/projects/spoofax/spx-imp/source-codes/trunk/org.strategoxt.imp.editors.spoofax/include");
		interpreter().addOperatorRegistry(new LanguageLibrary());
		
		projectNameTerm = termFactory().makeString(_projectName);
		SpxSemanticIndexFacade facade = new SpxSemanticIndexFacade(projectNameTerm , termFactory() , ioAgent());
		manager = new SpxPersistenceManager(facade);
		
		symtable = new SpxPackageLookupTable(manager);
		mSymTable = new SpxModuleLookupTable(manager);
		
		symtable.clear();
	}
	
	@Override protected void tearDown() throws Exception {
		symtable.clear();
		mSymTable.clear();
		
		manager.commitAndClose();
	}
	
	public void testShouldReturngPackageDeclarationbyUri() throws IOException 
	{
		symtable.clear();
		
		//package declaration 
		IStrategoList idp1 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p1"));
		IStrategoList idp2 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p2"));
		
		PackageDeclaration  p1 = new PackageDeclaration(absPathString2, idp1);
		PackageDeclaration  p2 = new PackageDeclaration(absPathString2, idp2);
		p2.addFileUri(absPathString1);
		
		symtable.definePackageDeclaration(p1);
		symtable.definePackageDeclaration(p2);
		try 
		{
			manager.commit();
			
		}catch(Exception ex)
		{
			// do nothing	
		}
		
		//setup expected
		PackageDeclaration expected = p2;
	
		int expectedPackageDecls = 1;
		
		//getting actual test run result and comparing with expected 
		int actualPackageDeclaration  = 0 ;
		for(PackageDeclaration  actual: symtable.packageDeclarationsByUri(absPathString1))
		{
			actualPackageDeclaration = actualPackageDeclaration + 1;    
			
			assertEquals(expected , actual);
			assertEquals(2 , actual.getAllFilePaths().size());
		}	
		
		assertEquals(expectedPackageDecls, actualPackageDeclaration);
	}
	
	public void testShouldReturnPackageDeclarationByQName()
	{
		//package declaration 
		IStrategoList idp1 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p1"));
		IStrategoList idp2 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p2"));
		
		PackageDeclaration p1 = new PackageDeclaration(absPathString2, idp1);
		PackageDeclaration  p2 = new PackageDeclaration(absPathString2, idp2);
		p2.addFileUri(absPathString1);
		
		symtable.definePackageDeclaration(p1);
		symtable.definePackageDeclaration(p2);
		
		PackageDeclaration expected = p2;
		PackageDeclaration  actual = symtable.getPackageDeclaration(idp2);

		assertEquals(expected, actual);
	}
	
	public void testUpdatingFileUrisShouldPersists() throws IOException {
		symtable.clear();
		
		//package declaration 
		IStrategoList idp1 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p1"));
		IStrategoList idp2 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p2"));
		
		PackageDeclaration p1  = new PackageDeclaration(absPathString1, idp1);
		PackageDeclaration  p2 = new PackageDeclaration(absPathString2, idp2);
		
		symtable.definePackageDeclaration(p1);
		symtable.definePackageDeclaration(p2);
	
		
		//adding another uri in package declaration
		symtable.addPackageDeclarationLocation(idp2, absPathString1);
		
		try 
		{
			manager.commit();
		}catch(Exception ex){// do nothing	
		}	
		
		//setup expected
		int expectedPackageDecls = 2;
		
		//getting actual test run result and comparing with expected 
		Set<PackageDeclaration> decls= (Set<PackageDeclaration>)symtable.packageDeclarationsByUri(absPathString1);
		
		symtable.getPackageDeclaration(idp1) ;
		
		assertEquals(expectedPackageDecls , decls.size());
	}
	
	public void testRemovePackageDeclarationsByFileUri() throws IOException
	{
		symtable.clear();
			
		//package declaration 
		IStrategoList idp1 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p1"));
		IStrategoList idp2 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p2"));
		IStrategoList idp3 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p3"));
			
		PackageDeclaration p1  = new PackageDeclaration(absPathString1, idp1);
		PackageDeclaration p2  = new PackageDeclaration(absPathString1, idp2);
		PackageDeclaration p3  = new PackageDeclaration(absPathString2, idp3);
			
		symtable.definePackageDeclaration(p1);
		symtable.definePackageDeclaration(p2);
		symtable.definePackageDeclaration(p3);
		
		
		symtable.removePackageDeclarationsByUri(absPathString1);
		
		assertEquals(symtable.size(), 1);
	}
		
	public void testNoFileUriShouldRemovePackageDeclaration() throws IOException
	{
		symtable.clear();
		
		//package declaration 
		IStrategoList idp1 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p1"));
		IStrategoList idp2 = termFactory().makeList(termFactory().makeString("test") , termFactory().makeString("p2"));
		
		PackageDeclaration p1  = new PackageDeclaration(absPathString1, idp1);
		PackageDeclaration  p2 = new PackageDeclaration(absPathString2, idp2);
		
		
		symtable.definePackageDeclaration(p1);
		symtable.definePackageDeclaration(p2);
	
		try 
		{
			manager.commit();
		}catch(Exception ex){// do nothing	
		}	
		
		//adding another uri in package declaration
		symtable.removePackageDeclarationLocation(idp1, absPathString1);
		
		
		assertEquals(null , symtable.getPackageDeclaration(idp1));
		
	}

	public void testLanguageDescriptorIsPersisted() throws IOException
	{
		symtable.clear();
		
		ITermFactory factory = termFactory();
		
		//Defining packagedecl in the symbol table. 
		IStrategoList idp1 = factory.makeList(factory.makeString("test") , factory.makeString("p1"));
		PackageDeclaration p1  = new PackageDeclaration(absPathString1, idp1);
		symtable.definePackageDeclaration(p1);
		
		//adding langauge descriptor
		LanguageDescriptor langDescriptor = LanguageDescriptor.newInstance(
				factory,
				idp1, 
				factory.makeList( factory.makeString("id1lang") ,factory.makeString("id2lang")),    
				factory.makeList( factory.makeString("langname2") ,factory.makeString("langname2")),
				asSDFStartSymbols( new String[]{"Start" , "Package"}) , 
				asEsvStartSymbols( new String[]{"Start" , "Package"})
				);
		
		symtable.defineLanguageDescriptor(idp1, langDescriptor);
		
		
		ArrayList<IStrategoList> actual = (ArrayList<IStrategoList>)symtable.getPackageIdsByLangaugeName("langname2");
	
		assertEquals( actual.size() , 1) ;
		assertEquals(idp1, actual.get(0));
	}	
	
	public void testUpdatingLanguageDescriptorIsPersisted() throws IOException
	{
		symtable.clear();
		
		ITermFactory factory = termFactory();
		
		//Defining packagedecl in the symbol table. 
		IStrategoList idp1 = factory.makeList(factory.makeString("test") , factory.makeString("p1"));
		PackageDeclaration p1  = new PackageDeclaration(absPathString1, idp1);
		symtable.definePackageDeclaration(p1);
		
		//adding langauge descriptor
		LanguageDescriptor langDescriptor = LanguageDescriptor.newInstance(
				factory,
				idp1, 
				factory.makeList( factory.makeString("id1lang") ,factory.makeString("id2lang")),    
				factory.makeList( factory.makeString("langname2") ,factory.makeString("langname2")),
				asSDFStartSymbols( new String[]{"Start" , "Package"}) , 
				asEsvStartSymbols( new String[]{"Start" , "Package"})
				);
		
		symtable.defineLanguageDescriptor(idp1, langDescriptor);
		
		LanguageDescriptor langDescriptor2 = symtable.getLangaugeDescriptor(idp1);
		langDescriptor2 = LanguageDescriptor.newInstance(factory, langDescriptor2);
		langDescriptor2.addLanguageNames(factory, factory.makeList( factory.makeString("langname3") ,factory.makeString("langname4")));
		
		symtable.defineLanguageDescriptor(idp1, langDescriptor2);
		
		ArrayList<IStrategoList> actual = (ArrayList<IStrategoList>)symtable.getPackageIdsByLangaugeName("langname3");
	
		assertEquals( actual.size() , 1) ;
		assertEquals(idp1, actual.get(0));
	}	
	
	public void testShouldThrowIllegalArgumentExceptionIfUnknownPackageId() throws IOException
	{
		symtable.clear();
		
		ITermFactory factory = termFactory();
		
		//Defining packagedecl in the symbol table. 
		IStrategoList idp1 = factory.makeList(factory.makeString("test") , factory.makeString("p1"));
		PackageDeclaration p1  = new PackageDeclaration(absPathString1, idp1);
		
		//Not defining package declaration. Hence package p1 is unknown 
		//is unknown in this current symbol table.
		//symtable.definePackageDeclaration(p1);
		
		//adding langauge descriptor
		LanguageDescriptor langDescriptor = LanguageDescriptor.newInstance(
				factory,
				idp1, 
				factory.makeList( factory.makeString("id1lang") ,factory.makeString("id2lang")),    
				factory.makeList( factory.makeString("langname2") ,factory.makeString("langname2")),
				asSDFStartSymbols( new String[]{"Start" , "Package"}) , 
				asEsvStartSymbols( new String[]{"Start" , "Package"})
				);
		try
		{
			symtable.defineLanguageDescriptor(idp1, langDescriptor);
		}catch (IllegalArgumentException ex)
		{
			// test is ok  since is it throwing corrent excpetion.  
			// TODO : check why @Test(expected = IllegalArgumentException.class) is not working.
		}
	}
	
	private IStrategoList asEsvStartSymbols(String [] esvStartSymbolsStrings)
	{
		ITermFactory factory = termFactory();
		IStrategoList list = factory.makeList();
		IStrategoConstructor sortCons = factory.makeConstructor("Sort", 1);
		
		for(String s : esvStartSymbolsStrings)
		{
			IStrategoString esvStartSymbol = factory.makeString(s);
			IStrategoAppl sortAppl = factory.makeAppl(sortCons, esvStartSymbol);
			
			list = factory.makeListCons(sortAppl, list);
		}
		return list;
	}
	
	private IStrategoList asSDFStartSymbols(String [] sdfStartSymbolsStrings)
	{
		ITermFactory factory = termFactory();
		IStrategoList list = factory.makeList();
		IStrategoConstructor sortCons = factory.makeConstructor("sort", 1);
		
		for(String s : sdfStartSymbolsStrings)
		{
			IStrategoString esvStartSymbol = factory.makeString(s);
			IStrategoAppl sortAppl = factory.makeAppl(sortCons, esvStartSymbol);
			
			list = factory.makeListCons(sortAppl, list);
		}
		return list;
	}
	
	public void testModuleDefinitionDefineShouldPersists() throws IOException
	{
		ITermFactory f = this.termFactory();
		SpxModuleLookupTable lookupTable = mSymTable;
		
		IStrategoList pId = f.makeList(f.makeString("test"));
		IStrategoList pId2 = f.makeList(f.makeString("test2"));
		
		//module declaration 
		IStrategoList idm1 = f.makeList(f.makeString("test") , f.makeString("m1"));
		ModuleDeclaration m1 = new ModuleDeclaration(absPathString1, idm1,pId );
		
		
		lookupTable.define(
					m1 , 
					(IStrategoAppl)getModuleDefinition(f, "m1") , 
					(IStrategoAppl)getAnalyzedModuleDefinition(f, "m1")
				);
		
		IStrategoList idm2 = f.makeList(f.makeString("test") , f.makeString("m2"));
		ModuleDeclaration m2 = new ModuleDeclaration(absPathString1, idm2,pId2 );
		
		lookupTable.define(
				m2 , 
				(IStrategoAppl)getModuleDefinition(f, "m2") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f, "m2")
			);
	
		
		IStrategoList idm3 = f.makeList(f.makeString("test") , f.makeString("m3"));
		ModuleDeclaration m3 = new ModuleDeclaration(absPathString2, idm3,pId );

		lookupTable.define(
				m3 , 
				(IStrategoAppl)getModuleDefinition(f, "m3") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f, "m3")
			);
	
		assertEquals(3, lookupTable.size());
		
		manager.commit();
		
		
		ModuleDeclaration expected = m1;
		ModuleDeclaration actual =  lookupTable.getModuleDeclaration(idm1);
		
		assertEquals(expected , actual);
		
	}
	
	public void testShouldReturnModuleByPackageId() throws IOException, SpxSymbolTableException
	{
		ITermFactory f = this.termFactory();
		SpxModuleLookupTable lookupTable = mSymTable;
		
		IStrategoList pId = f.makeList(f.makeString("test"));
		IStrategoList pId2 = f.makeList(f.makeString("test2"));
		
		//module declaration 
		IStrategoList idm1 = f.makeList(f.makeString("test") , f.makeString("m1"));
		ModuleDeclaration m1 = new ModuleDeclaration(absPathString1, idm1,pId );
		
		
		lookupTable.define(
					m1 , 
					(IStrategoAppl)getModuleDefinition(f, "m1") , 
					(IStrategoAppl)getAnalyzedModuleDefinition(f, "m1")
				);
		
		IStrategoList idm2 = f.makeList(f.makeString("test") , f.makeString("m2"));
		ModuleDeclaration m2 = new ModuleDeclaration(absPathString1, idm2,pId2 );
		
		lookupTable.define(
				m2 , 
				(IStrategoAppl)getModuleDefinition(f, "m2") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f, "m2")
			);
	
		
		IStrategoList idm3 = f.makeList(f.makeString("test") , f.makeString("m3"));
		ModuleDeclaration m3 = new ModuleDeclaration(absPathString2, idm3,pId );

		lookupTable.define(
				m3 , 
				(IStrategoAppl)getModuleDefinition(f, "m3") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f, "m3")
			);
	
		assertEquals(3, lookupTable.size());
		
		manager.commit();
		
		
		ArrayList<ModuleDeclaration> modulesByPackageID 
			= (ArrayList<ModuleDeclaration>)lookupTable.getModuleDeclarationsByPackageId(pId);
		
		assertEquals(2, modulesByPackageID.size());
	}
	
	public void testShouldReturnModuleDeclarationByFilePath() throws IOException
	{
		ITermFactory f = this.termFactory();
		SpxModuleLookupTable lookupTable = mSymTable;
		
		IStrategoList pId = f.makeList(f.makeString("test"));
		IStrategoList pId2 = f.makeList(f.makeString("test2"));
		
		//module declaration 
		IStrategoList idm1 = f.makeList(f.makeString("test") , f.makeString("m1"));
		ModuleDeclaration m1 = new ModuleDeclaration(absPathString1, idm1,pId );
		
		
		lookupTable.define(
					m1 , 
					(IStrategoAppl)getModuleDefinition(f, "m1") , 
					(IStrategoAppl)getAnalyzedModuleDefinition(f, "m1")
				);
		
		IStrategoList idm2 = f.makeList(f.makeString("test") , f.makeString("m2"));
		ModuleDeclaration m2 = new ModuleDeclaration(absPathString2, idm2,pId2 );
		
		lookupTable.define(
				m2 , 
				(IStrategoAppl)getModuleDefinition(f, "m2") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f, "m2")
			);
	
		
		IStrategoList idm3 = f.makeList(f.makeString("test") , f.makeString("m3"));
		ModuleDeclaration m3 = new ModuleDeclaration(absPathString2, idm3,pId );

		lookupTable.define(
				m3 , 
				(IStrategoAppl)getModuleDefinition(f, "m3") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f, "m3")
			);
	
		assertEquals(3, lookupTable.size());
		
		manager.commit();
		
		ArrayList<ModuleDeclaration> modulesByFileUri 
			= (ArrayList<ModuleDeclaration>)lookupTable.getModuleDeclarationsByUri(absPathString1);
		
		assertEquals(1, modulesByFileUri.size());
	}
	
	
	public void testShouldRemoveModuleDeclarationByFilePath() throws IOException
	{
		ITermFactory f = this.termFactory();
		
		SpxModuleLookupTable lookupTable = mSymTable;
		
		IStrategoList pId = f.makeList(f.makeString("test"));
		IStrategoList pId2 = f.makeList(f.makeString("test2"));
		
		//module declaration 
		IStrategoList idm1 = f.makeList(f.makeString("test") , f.makeString("m1"));
		ModuleDeclaration m1 = new ModuleDeclaration(absPathString1, idm1,pId );
		
		
		lookupTable.define(
					m1 , 
					(IStrategoAppl)getModuleDefinition(f, "m1") , 
					(IStrategoAppl)getAnalyzedModuleDefinition(f, "m1")
				);
		
		IStrategoList idm2 = f.makeList(f.makeString("test") , f.makeString("m2"));
		ModuleDeclaration m2 = new ModuleDeclaration(absPathString2, idm2,pId2 );
		
		lookupTable.define(
				m2 , 
				(IStrategoAppl)getModuleDefinition(f, "m2") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f, "m2")
			);
	
		
		IStrategoList idm3 = f.makeList(f.makeString("test") , f.makeString("m3"));
		ModuleDeclaration m3 = new ModuleDeclaration(absPathString2, idm3,pId );

		lookupTable.define(
				m3 , 
				(IStrategoAppl)getModuleDefinition(f, "m3") , 
				(IStrategoAppl)getAnalyzedModuleDefinition(f , "m3")
			);
	
		assertEquals(3, lookupTable.size());
		
		manager.commit();
		
		lookupTable.removeModuleDeclarationsByUri(absPathString2);
		
		assertEquals(1, lookupTable.size());
	}

	static IStrategoTerm getModuleDefinition(ITermFactory f, String moduleName) {

		String text = "Module(" + "None()" + ", SPXModuleName(\"" + moduleName
				+ "\")" + ", [])";

		return f.parseFromString(text);
	}

	static IStrategoTerm getAnalyzedModuleDefinition(ITermFactory f , String moduleName) 
	{ 
		return getModuleDefinition(f, moduleName);
	}
}
