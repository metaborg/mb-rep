package org.spoofax.interpreter.library.language.tests;

import java.io.IOException;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.SemanticIndex;
import org.spoofax.interpreter.library.language.spxlang.SpxSemanticIndex;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.test.AbstractInterpreterTest;

/**
 * Integration tests to to test various operation of semantic index
 * of SpoofaxLang. 
 * 
 * @author Md. Adil Akhter
 * Created On : Sep 2, 2011
 */
public class SpxSemanticIndexTests extends AbstractInterpreterTest{
	
	private final SpxSemanticIndex _index = new SpxSemanticIndex();
	private final String _projectName = "test";
	private IStrategoString projectNameTerm; 
	
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
		
		_index.initialize( projectNameTerm , termFactory(), ioAgent());
	}
	
	@Override protected void tearDown() throws Exception {
		
		_index.save(projectNameTerm);
		_index.close(projectNameTerm);
	}

	
}
