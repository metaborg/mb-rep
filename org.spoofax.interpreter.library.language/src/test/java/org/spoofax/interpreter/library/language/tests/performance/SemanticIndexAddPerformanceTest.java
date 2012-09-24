package org.spoofax.interpreter.library.language.tests.performance;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 25, warmupRounds = 5, callgc = true, clock = Clock.CPU_TIME)
public class SemanticIndexAddPerformanceTest extends SemanticIndexPerformanceTest {
  @Rule
  public MethodRule benchmarkRun = new BenchmarkRule();
  
  
  /// Add tests with 1 file.
  @Test 
  public void add500_1()
  {
    add(100, 1);
  }
  
  @Test
  public void add5000_1()
  {
    add(1000, 1);
  }
  
  @Test
  public void add50000_1()
  {
    add(10000, 1);
  }
  
  @Test
  public void add500000_1()
  {
    add(100000, 1);
  }
  
  @Test
  public void add1000000_1()
  {
    add(200000, 1);
  }
  
  @Test
  public void add2500000_1()
  {
    add(500000, 1);
  }
  
  @Test
  public void add5000000_1()
  {
    add(1000000, 1);
  }
  
  @Test
  public void add10000000_1()
  {
    add(2000000, 1);
  }
  
  
  /// Add tests with 10 files.
  @Test
  public void add500_10()
  {
    add(100, 10);
  }
  
  @Test
  public void add5000_10()
  {
    add(1000, 10);
  }
  
  @Test
  public void add50000_10()
  {
    add(10000, 10);
  }
  
  @Test
  public void add500000_10()
  {
    add(100000, 10);
  }
  
  @Test
  public void add1000000_10()
  {
    add(200000, 10);
  }
  
  @Test
  public void add2500000_10()
  {
    add(500000, 10);
  }
  
  @Test
  public void add5000000_10()
  {
    add(1000000, 10);
  }
  
  @Test
  public void add10000000_10()
  {
    add(2000000, 10);
  }
  
  
  /// Add tests with 100 files.
  @Test
  public void add500_100()
  {
    add(100, 100);
  }
  
  @Test
  public void add5000_100()
  {
    add(1000, 100);
  }
  
  @Test
  public void add50000_100()
  {
    add(10000, 100);
  }
  
  @Test
  public void add500000_100()
  {
    add(100000, 100);
  }
  
  @Test
  public void add1000000_100()
  {
    add(200000, 100);
  }
  
  @Test
  public void add2500000_100()
  {
    add(500000, 100);
  }
  
  @Test
  public void add5000000_100()
  {
    add(1000000, 100);
  }
  
  @Test
  public void add10000000_100()
  {
    add(2000000, 100);
  }
  
  
  public void add(int numItems, int numFiles)
  {
    for(int i = 0; i < numItems; ++i)
    {
      index.add(def1, getFile(numFiles));
      index.add(def2, getFile(numFiles));
      index.add(def3, getFile(numFiles));
      index.add(use1, getFile(numFiles));
      index.add(type1, getFile(numFiles));
    }
  }
}
