package org.spoofax.interpreter.library.language.tests;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndexFacade;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.test.AbstractInterpreterTest;

public class SpxSemanticIndexFacadeTest extends AbstractInterpreterTest{
	
	private final String _projectName = "test";
	
	private IStrategoString projectNameTerm; 
	private SpxSemanticIndexFacade _facade;
	
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
		
	}
	
	@Override protected void tearDown() throws Exception {
		_facade.close();
	}
	
	public void testListenerRemovingRecordsFromChildSymbolTables()
	{
		
	}	
}
