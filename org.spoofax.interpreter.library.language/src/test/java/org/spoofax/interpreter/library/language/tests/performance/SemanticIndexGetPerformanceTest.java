package org.spoofax.interpreter.library.language.tests.performance;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.spoofax.interpreter.library.language.SemanticIndexEntry;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 25, warmupRounds = 5, callgc = true, clock = Clock.CPU_TIME)
public class SemanticIndexGetPerformanceTest extends SemanticIndexPerformanceTest {
  @Rule
  public MethodRule benchmarkRun = new BenchmarkRule();
  
  
  /// Get tests with 1 file.
  @Test 
  public void get500_1()
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
  public void get500_10()
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
  public void get500_100()
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
}
