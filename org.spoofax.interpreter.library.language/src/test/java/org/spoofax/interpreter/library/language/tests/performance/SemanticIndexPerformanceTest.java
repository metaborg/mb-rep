package org.spoofax.interpreter.library.language.tests.performance;

import org.junit.Before;
import org.junit.BeforeClass;
import org.spoofax.interpreter.library.language.SemanticIndexFileDescriptor;
import org.spoofax.interpreter.library.language.tests.SemanticIndexTest;
import org.spoofax.interpreter.terms.IStrategoAppl;

public class SemanticIndexPerformanceTest extends SemanticIndexTest {
  private static int MAX_NUM_FILES = 1000;

  public static IStrategoAppl def1;
  public static IStrategoAppl def2;
  public static IStrategoAppl def3;
  public static IStrategoAppl use1;
  public static IStrategoAppl type1;
  public static IStrategoAppl typeTemplate1;
  public static SemanticIndexFileDescriptor[] files;
  public static int fileIndex;

  @BeforeClass
  public static void setUpOnce() {
    SemanticIndexTest.setUpOnce();

    def1 = def("Class", "java", "lang", "String");
    def2 = def("Method", "java", "lang", "System", "out", "println");
    def3 = def("Field", "java", "lang", "array", "Length");
    use1 = use("Class", "java", "lang", "System");
    type1 = type(constructor("Type", str("String")), "Method", "java", "lang",
        "Object", "toString");
    typeTemplate1 = type(tuple(), "Method", "java", "lang", "Object",
        "toString");

    files = new SemanticIndexFileDescriptor[MAX_NUM_FILES];
    for (int i = 0; i < MAX_NUM_FILES; ++i) {
      files[i] = SemanticIndexFileDescriptor.fromTerm(agent, file("File" + i));
    }

    fileIndex = -1;
  }
  
  @Before
  public void setUp() {
    fileIndex = -1;
  }

  public static SemanticIndexFileDescriptor getFile(int numFiles) {
    SemanticIndexFileDescriptor file = files[++fileIndex];
    if (fileIndex == numFiles - 1)
      fileIndex = -1;
    return file;
  }
}
