package org.spoofax.interpreter.library.language.tests.performance;

import org.junit.Before;
import org.spoofax.interpreter.library.language.SemanticIndexFileDescriptor;
import org.spoofax.interpreter.library.language.tests.SemanticIndexTest;
import org.spoofax.interpreter.terms.IStrategoAppl;

public class SemanticIndexPerformanceTest extends SemanticIndexTest {
  private static int MAX_NUM_FILES = 100;
  
  protected IStrategoAppl def1;
  protected IStrategoAppl def2;
  protected IStrategoAppl def3;
  protected IStrategoAppl use1;
  protected IStrategoAppl type1;
  protected IStrategoAppl typeTemplate1;
  protected SemanticIndexFileDescriptor[] files;
  protected int fileIndex;
  
  @Before
  public void setUp()
  {
    super.setUp();
    
    def1 = def("Class", "java", "lang", "String");
    def2 = def("Method", "java", "lang", "System", "out", "println");
    def3 = def("Field", "java", "lang", "array", "Length");
    use1 = use("Class", "java", "lang", "System");
    type1 = type(constructor("Type", str("String")), "Method", "java", "lang", "Object", "toString");
    typeTemplate1 = type(tuple(), "Method", "java", "lang", "Object", "toString");
    
    files = new SemanticIndexFileDescriptor[MAX_NUM_FILES];
    for(int i = 0; i < MAX_NUM_FILES; ++i)
    {
      files[i] = setupIndex(file("File" + i));
    }
    
    fileIndex = -1;
  }
  
  public SemanticIndexFileDescriptor getFile(int numFiles)
  {
    SemanticIndexFileDescriptor file = files[++fileIndex];
    if(fileIndex == numFiles - 1)
      fileIndex = -1;
    return file;
  }
}
