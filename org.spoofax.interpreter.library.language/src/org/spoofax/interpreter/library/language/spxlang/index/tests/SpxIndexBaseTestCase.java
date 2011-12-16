package org.spoofax.interpreter.library.language.spxlang.index.tests;


import junit.framework.TestCase;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.language.spxlang.index.SpxIndexConfiguration;
import org.spoofax.interpreter.terms.ITermFactory;

public abstract class SpxIndexBaseTestCase extends TestCase {

    protected Interpreter itp;
    protected ITermFactory factory;
    protected String basePath;

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
}
