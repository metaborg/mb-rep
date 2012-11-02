package org.spoofax.interpreter.library.language.spxlang.index.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.LanguageLibrary;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableEntry;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class SpxSymbolTableEntryTests extends SpxIndexBaseTestCase{
	private final String _projectName = ".UnitTest3";
	private SpxSemanticIndexFacade _facade;
	
	final String absPathString1 = "c:/temp/test1.spx" ;
	final String absPathString2 = "c:/temp/test2.spx" ;

	private Interpreter interpreter(){ return itp;	}

	private IOAgent ioAgent() { return itp.getIOAgent(); }
	
	private ITermFactory termFactory() { return factory; 	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		interpreter().addOperatorRegistry(new LanguageLibrary());
		
		_facade = new SpxSemanticIndexFacade(getProjectPath(_projectName) , termFactory() , ioAgent());
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
		assertEquals(_facade.getCons().getGlobalNamespaceTypeCon().getName() , entry.value.getSignatureString());
		assertEquals(_facade.getCons().getGlobalNamespaceTypeCon(), entry.value.typeCons(_facade));
		assertTrue(entry.key != null);
	}
	
	private IStrategoAppl indexTestPackageDecl(String packageName , String fileName) {
		IStrategoAppl pQnameAppl = (IStrategoAppl)termFactory().parseFromString("Package(QName(["+packageName+"]))");
		return pQnameAppl;
	}

	
	public void testSpoofaxSymbolTableEntryBuilder2() {
		
		
		SpxKey k1 = (SpxKey) new SpxOverloadedKey("Test" ,1);
		SpxKey k2 = (SpxKey) new SpxOverloadedKey("Test" ,2);
		
		Map<SpxKey, SpxKey> symbols = new HashMap<SpxKey, SpxKey>();
	
		symbols.put(k1, k1);
		symbols.put(k2, k2);
		
		SpxKey k3 = symbols.get(new SpxKey("Test"));
		
		
		System.out.println(k3);
	}

	
	
	
}

class SpxOverloadedKey extends SpxKey{
	int value;
	
	public SpxOverloadedKey(String s){
		super(s);
	}
	public SpxOverloadedKey(String s, int value){
		this(s);
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}

class SpxKey{
	
	String test;
	public SpxKey( String s){
		test = s;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((test == null) ? 0 : test.hashCode());
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
		if (!(obj instanceof SpxKey))
			return false;
		
		SpxKey other = (SpxKey) obj;
		if (test == null) {
			if (other.test != null)
				return false;
		} else if (!test.equals(other.test))
			return false;
		return true;
	}
	
}