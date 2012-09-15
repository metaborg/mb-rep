package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.ITermFactory;

import junit.framework.TestCase;

public class SemanticIndexTestCase extends TestCase {
  protected Interpreter interpreter;
  protected ITermFactory factory;
  protected IOAgent agent;
  protected SemanticIndexManager indexManager = new SemanticIndexManager();
  
  protected void setUp() throws Exception {
    super.setUp();
    
    interpreter = new Interpreter();
    factory = interpreter.getFactory();
    agent = interpreter.getIOAgent();
  }
  
  @Override
  protected void tearDown() throws Exception {
    interpreter.shutdown();
    interpreter = null;
    factory = null;
    agent = null;
    
    super.tearDown();
  }
}
