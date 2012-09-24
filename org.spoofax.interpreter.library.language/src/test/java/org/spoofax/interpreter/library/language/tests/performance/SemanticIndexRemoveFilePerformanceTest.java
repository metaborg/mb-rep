package org.spoofax.interpreter.library.language.tests.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 3, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class SemanticIndexRemoveFilePerformanceTest extends
    SemanticIndexPerformanceTest {
  @Rule
  public MethodRule benchmarkRun;

  private int numItemsPerFile;
  private int numFiles;
  private boolean deleteAll;
  
  @Parameters
  public static List<Object[]> data() {
    Object[][] data = new Object[][] { 
      { 20 , 1   , false }
    , { 100, 1   , false }
    , { 500, 1   , false }
    , { 20 , 10  , false }
    , { 100, 10  , false }
    , { 500, 10  , false }
    , { 20 , 100 , false }
    , { 100, 100 , false }
    , { 500, 100 , false }
    , { 20 , 500 , false }
    , { 100, 500 , false }
    , { 500, 500 , false }
    , { 20 , 1000, false }
    , { 100, 1000, false }
    , { 500, 1000, false }
    };
    return Arrays.asList(data);
  }
  
  public SemanticIndexRemoveFilePerformanceTest(int numItemsPerFile, int numFiles, 
      boolean deleteAll) {
    this.numItemsPerFile = numItemsPerFile;
    this.numFiles = numFiles;
    this.deleteAll = deleteAll;

    try {
      benchmarkRun = new BenchmarkRule(new CSVResultsConsumer(
          (this.numItemsPerFile * 5) + "," + this.numFiles, new FileWriter("remove.csv", true)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    for(int f = 0; f < numFiles; ++f) {
      for (int i = 0; i < numItemsPerFile; ++i) {
        index.add(def1, files[f]);
        index.add(def2, files[f]);
        index.add(def3, files[f]);
        index.add(use1, files[f]);
        index.add(type1, files[f]);
      }
    }
  }
  
  @Test
  public void removeFile() {
    if(deleteAll) {
      for(int i = 0; i < numFiles; ++i) {
        index.removeFile(files[i]);
      }
    } else {
      index.removeFile(files[1]);
    }
  }
}
