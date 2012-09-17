package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.util.Collection;

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

    language = str("TestLanguage");
    projectPath = str("TestPath");
    project = SemanticIndexFileDescriptor.fromTerm(agent, projectPath);

    indexManager = new SemanticIndexManager();
    indexManager.loadIndex(asJavaString(language), project.getURI(), factory,
        agent);
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

  public IStrategoString str(String str) {
    return factory.makeString(str);
  }

  public IStrategoAppl constructor(String constructor, IStrategoTerm... terms) {
    return factory.makeAppl(factory.makeConstructor(constructor, terms.length),
        terms);
  }

  public IStrategoString file(String file) {
    return str(file);
  }

  public IStrategoTuple file(String file, String namespace, String... path) {
    return factory.makeTuple(str(file), uri(namespace, path));
  }

  public IStrategoList path(String... path) {
    IStrategoString[] strategoPath = new IStrategoString[path.length];
    for (int i = 0; i < path.length; ++i)
      // Paths are reversed in Stratego for easy appending of new names.
      strategoPath[i] = str(path[path.length - i - 1]);
    return factory.makeList(strategoPath);
  }

  public IStrategoList uri(String namespace, String... path) {
    return factory.makeListCons(constructor(namespace), path(path));
  }

  public IStrategoAppl def(String namespace, String... path) {
    return factory.makeAppl(factory.makeConstructor("Def", 1),
        uri(namespace, path));
  }

  public IStrategoAppl use(String namespace, String... path) {
    return factory.makeAppl(factory.makeConstructor("Use", 1),
        uri(namespace, path));
  }

  public IStrategoAppl read(String namespace, String... path) {
    return factory.makeAppl(factory.makeConstructor("Read", 1),
        uri(namespace, path));
  }

  public IStrategoAppl readAll(String prefix, String namespace, String... path) {
    return factory.makeAppl(factory.makeConstructor("ReadAll", 2),
        uri(namespace, path), str(prefix));
  }

  public IStrategoAppl type(IStrategoTerm type, String namespace,
      String... path) {
    return factory.makeAppl(factory.makeConstructor("ReadAll", 2),
        uri(namespace, path), type);
  }

  public boolean contains(Collection<SemanticIndexEntry> entries,
      IStrategoTerm term) {
    boolean found = false;
    for (SemanticIndexEntry entry : entries)
      found = found || entry.toTerm(factory).match(term);
    return found;
  }

  public boolean matchAll(Collection<SemanticIndexEntry> entries,
      IStrategoTerm term) {
    if (entries.size() == 0)
      return false;
    boolean matchAll = true;
    for (SemanticIndexEntry entry : entries)
      matchAll = matchAll && entry.toTerm(factory).match(term);
    return matchAll;
  }
}
