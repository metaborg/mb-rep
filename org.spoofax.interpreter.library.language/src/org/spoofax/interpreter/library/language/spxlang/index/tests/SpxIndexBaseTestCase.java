package org.spoofax.interpreter.library.language.spxlang.index.tests;


import junit.framework.TestCase;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.language.spxlang.index.SpxIndexConfiguration;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.ITermFactory;

public abstract class SpxIndexBaseTestCase extends TestCase {

    protected Interpreter itp;
    protected ITermFactory factory;
    protected String basePath;

    final String  projectRoot = "UnitTestsRoot";
    
    protected void setUp() throws Exception {
    	super.setUp();
    	
    	SpxIndexConfiguration.setLoggingSymbols(false);
    	SpxIndexConfiguration.setTracing(false);
        
    	itp = new Interpreter();
        factory = itp.getFactory();
     }
    
    @Override
    protected void tearDown() throws Exception {
        itp.shutdown();
        itp = null;
        factory = null;
        super.tearDown();
    }
    
    protected IStrategoString getProjectPath(String projectName) {
    	return factory.makeString(projectRoot+ "/" + projectName); 
    }
}
