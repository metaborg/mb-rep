package org.spoofax.interpreter.library.language;

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.spoofax.interpreter.terms.IStrategoAppl;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 25, warmupRounds = 5, callgc = true, clock = Clock.CPU_TIME)
public class SemanticIndexPerformanceTest extends SemanticIndexTest {
  @Rule
  public MethodRule benchmarkRun = new BenchmarkRule();
  
  private static int NUM_FILES = 100;
  
  private static IStrategoAppl def1;
  private static IStrategoAppl def2;
  private static IStrategoAppl def3;
  private static IStrategoAppl use1;
  private static IStrategoAppl type1;
  private static IStrategoAppl typeTemplate1;
  private static SemanticIndexFileDescriptor[] files;
  private static int fileIndex;
  
  @BeforeClass
  public static void setUpOnce()
  {
    SemanticIndexTest.setUpOnce();
    def1 = def("Class", "java", "lang", "String");
    def2 = def("Method", "java", "lang", "System", "out", "println");
    def3 = def("Field", "java", "lang", "array", "Length");
    use1 = use("Class", "java", "lang", "System");
    type1 = type(constructor("Type", str("String")), "Method", "java", "lang", "Object", "toString");
    typeTemplate1 = type(tuple(), "Method", "java", "lang", "Object", "toString");
  }
  
  @Before
  public void setUp()
  {
    super.setUp();
    
    files = new SemanticIndexFileDescriptor[NUM_FILES];
    for(int i = 0; i < NUM_FILES; ++i)
    {
      files[i] = setupIndex(file("File" + i));
    }
    
    fileIndex = -1;
  }
  
  /// Get tests with 1 file.
  @Test 
  public void get_500_1()
  {
    get(100, 1);
  }
  
  @Test
  public void get5000_1()
  {
    get(1000, 1);
  }
  
  @Test
  public void get50000_1()
  {
    get(10000, 1);
  }
  
  @Test
  public void get500000_1()
  {
    get(100000, 1);
  }
  
  @Test
  public void get1000000_1()
  {
    get(200000, 1);
  }
  
  @Test
  public void get2500000_1()
  {
    get(500000, 1);
  }
  
  @Test
  public void get5000000_1()
  {
    get(1000000, 1);
  }
  
  @Test
  public void get10000000_1()
  {
    get(2000000, 1);
  }
  
  
  /// Get tests with 10 files.
  @Test
  public void get_500_10()
  {
    get(100, 10);
  }
  
  @Test
  public void get5000_10()
  {
    get(1000, 10);
  }
  
  @Test
  public void get50000_10()
  {
    get(10000, 10);
  }
  
  @Test
  public void get500000_10()
  {
    get(100000, 10);
  }
  
  @Test
  public void get1000000_10()
  {
    get(200000, 10);
  }
  
  @Test
  public void get2500000_10()
  {
    get(500000, 10);
  }
  
  @Test
  public void get5000000_10()
  {
    get(1000000, 10);
  }
  
  @Test
  public void get10000000_10()
  {
    get(2000000, 10);
  }
  
  
/// Get tests with 100 files.
  @Test
  public void get_500_100()
  {
    get(100, 100);
  }
  
  @Test
  public void get5000_100()
  {
    get(1000, 100);
  }
  
  @Test
  public void get50000_100()
  {
    get(10000, 100);
  }
  
  @Test
  public void get500000_100()
  {
    get(100000, 100);
  }
  
  @Test
  public void get1000000_100()
  {
    get(200000, 100);
  }
  
  @Test
  public void get2500000_100()
  {
    get(500000, 100);
  }
  
  @Test
  public void get5000000_100()
  {
    get(1000000, 100);
  }
  
  @Test
  public void get10000000_100()
  {
    get(2000000, 100);
  }
  
  
  public void get(int numItems, int numFiles)
  {
    
    for(int i = 0; i < numItems; ++i)
    {
      index.add(def1, getFile(numFiles));
      index.add(def2, getFile(numFiles));
      index.add(def3, getFile(numFiles));
      index.add(use1, getFile(numFiles));
      index.add(type1, getFile(numFiles));
    }
    
    @SuppressWarnings("unused")
    Collection<SemanticIndexEntry> ret;
    for(int i = 0; i < numItems; ++i)
    {
      ret = index.getEntries(def1);
      ret = index.getEntries(def2);
      ret = index.getEntries(def3);
      ret = index.getEntries(use1);
      ret = index.getEntries(typeTemplate1);
    }
  }
  
  public static SemanticIndexFileDescriptor getFile(int numFiles)
  {
    SemanticIndexFileDescriptor file = files[++fileIndex];
    if(fileIndex == numFiles - 1)
      fileIndex = -1;
    return file;
  }
}
