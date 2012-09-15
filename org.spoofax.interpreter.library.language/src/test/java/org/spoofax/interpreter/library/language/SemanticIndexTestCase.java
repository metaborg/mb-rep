package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import org.spoofax.interpreter.core.Interpreter;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import junit.framework.TestCase;

public class SemanticIndexTestCase extends TestCase {
  protected Interpreter interpreter;
  protected ITermFactory factory;
  protected IOAgent agent;

  protected IStrategoString language;
  protected IStrategoString projectPath;
  protected IStrategoTerm fileTerm;
  
  protected SemanticIndexManager indexManager;
  protected SemanticIndexFileDescriptor project;
  protected ISemanticIndex index;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    interpreter = new Interpreter();
    factory = interpreter.getFactory();
    agent = interpreter.getIOAgent();
    
    language = factory.makeString("TestLanguage");
    projectPath = factory.makeString("TestPath");
    project = SemanticIndexFileDescriptor.fromTerm(agent, projectPath);
    
    indexManager = new SemanticIndexManager();
    indexManager.loadIndex(asJavaString(language), project.getURI(), factory, agent);
    index = indexManager.getCurrent();
    index.initialize(factory, agent);
  }
  
  @Override
  protected void tearDown() throws Exception {
    index.clear();
    index = null;
    indexManager = null;
    
    language = null;
    projectPath = null;
    fileTerm = null;
    project = null;
    
    interpreter.shutdown();
    interpreter = null;
    factory = null;
    agent = null;
    
    super.tearDown();
  }
  
  public IStrategoString file(String file)
  {
    return factory.makeString(file);
  }
  
  public IStrategoTuple file(String file, String namespace, String... path)
  {
    return factory.makeTuple(factory.makeString(file), uri(namespace, path));
  }
  
  public IStrategoAppl namespace(String namespace)
  {
    return factory.makeAppl(factory.makeConstructor(namespace, 0));
  }
  
  public IStrategoList path(String... path)
  {
    IStrategoString[] strategoPath = new IStrategoString[path.length];
    for(int i = 0; i < path.length; ++i)
      strategoPath[i] = factory.makeString(path[i]);
    return factory.makeList(strategoPath);
  }
  
  public IStrategoList uri(String namespace, String... path)
  {
    return factory.makeListCons(namespace(namespace), path(path));
  }
  
  public IStrategoAppl def(String namespace, String... path)
  {
    return factory.makeAppl(factory.makeConstructor("Def", 1), uri(namespace, path));
  }
  
  public IStrategoAppl use(String namespace, String... path)
  {
    return factory.makeAppl(factory.makeConstructor("Use", 1), uri(namespace, path));
  }
  
  public IStrategoAppl read(String namespace, String... path)
  {
    return factory.makeAppl(factory.makeConstructor("Read", 1), uri(namespace, path));
  }
}
