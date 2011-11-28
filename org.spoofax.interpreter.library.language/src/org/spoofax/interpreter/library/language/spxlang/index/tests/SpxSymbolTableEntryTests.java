package org.spoofax.interpreter.library.language.spxlang.index.tests;

import java.io.IOException;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableEntry;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.test.AbstractInterpreterTest;

public class SpxSymbolTableEntryTests extends AbstractInterpreterTest{
	private final String _projectName = "test-sybol-table";
	
	private IStrategoString projectNameTerm; 
	private SpxSemanticIndexFacade _facade;
	
	final String absPathString1 = "c:/temp/test1.spx" ;
	final String absPathString2 = "c:/temp/test2.spx" ;

	private Interpreter interpreter(){ return itp;	}

	private IOAgent ioAgent() { return itp.getIOAgent(); }
	
	private ITermFactory termFactory() { return factory; 	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp("C:/work/projects/spoofax/spx-imp/source-codes/trunk/org.strategoxt.imp.editors.spoofax/include");
		interpreter().addOperatorRegistry(new LanguageLibrary());
		
		projectNameTerm = termFactory().makeString(_projectName);
		
	
		_facade = new SpxSemanticIndexFacade(projectNameTerm , termFactory() , ioAgent());
		_facade.cleanIndexAndSymbolTable();
	}
	
	public void testSpoofaxSymbolTableEntryBuilder() throws IOException{

		String packageName1 =  	"\"languages\", \"entitylang\"" ;
		IStrategoAppl data = indexTestPackageDecl(packageName1, absPathString1);
		IStrategoTerm id =  termFactory().parseFromString(packageName1);
		
		SpxSymbolTableEntry entry = 
			SpxSymbolTableEntry.newEntry()
						  .with(id)
						  .instanceOf(_facade.getCons().getGlobalNamespaceTypeCon())	
					      .uses(_facade.getTermAttachmentSerializer())
					      .data(data)
					      .build();
		
		assertTrue(entry.value != null);
		assertTrue(entry.value.namespaceUri() == null);
		assertEquals(_facade.getCons().getGlobalNamespaceTypeCon().getName() , entry.value.type());
		assertEquals(_facade.getCons().getGlobalNamespaceTypeCon(), entry.value.typeCons(_facade));
		assertTrue(entry.key != null);
	}
	
	private IStrategoAppl indexTestPackageDecl(String packageName , String fileName) {
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageName+"]))");
		return pQnameAppl;
	}

	
}
