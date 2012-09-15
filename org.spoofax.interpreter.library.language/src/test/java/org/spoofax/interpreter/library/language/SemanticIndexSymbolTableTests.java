package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SemanticIndexSymbolTableTests extends SemanticIndexTestCase 
{
  protected IStrategoString language = factory.makeString("TestLanguage");
  protected IStrategoString projectPath = factory.makeString("TestPath");
  protected IStrategoTerm fileTerm = factory.makeString("TestFile");
  protected ISemanticIndex index;

  protected void init()
  {
    SemanticIndexFileDescriptor project = SemanticIndexFileDescriptor.fromTerm(agent, projectPath);
    SemanticIndexFileDescriptor file = SemanticIndexFileDescriptor.fromTerm(agent, fileTerm);
    indexManager.loadIndex(asJavaString(language), project.getURI(), factory, agent);
    indexManager.setCurrentFile(file);
    index = indexManager.getCurrent();
    index.initialize(factory, agent);
  }
}
