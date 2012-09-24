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

@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class SemanticIndexRemoveFilePerformanceTest extends
    SemanticIndexPerformanceTest {
  @Rule
  public MethodRule benchmarkRun;

  private int numItemsPerFile;
  private int numFiles;
  
  @Parameters
  public static List<Object[]> data() {
    Object[][] data = new Object[][] { 
      { 20 , 1    }
    , { 100, 1    }
    , { 200, 1    }
    , { 300, 1    }
    , { 400, 1    }
    , { 500, 1    }
    , { 20 , 10   }
    , { 100, 10   }
    , { 200, 10   }
    , { 300, 10   }
    , { 400, 10   }
    , { 500, 10   }
    , { 20 , 100  }
    , { 100, 100  }
    , { 200, 100  }
    , { 300, 100  }
    , { 400, 100  }
    , { 500, 100  }
    , { 20 , 500  }
    , { 100, 500  }
    , { 200, 500  }
    , { 300, 500  }
    , { 400, 500  }
    , { 500, 500  }
    , { 20 , 1000 }
    , { 100, 1000 }
    , { 200, 1000 }
    , { 300, 1000 }
    , { 400, 1000 }
    , { 500, 1000 }
    };
    return Arrays.asList(data);
  }
  
  public SemanticIndexRemoveFilePerformanceTest(int numItemsPerFile, int numFiles) {
    this.numItemsPerFile = numItemsPerFile;
    this.numFiles = numFiles;

    try {
      benchmarkRun = new BenchmarkRule(new CSVResultsConsumer(
          (this.numItemsPerFile * 5) + "," + this.numFiles, new FileWriter("remove.csv", true)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    index.clear();
    
    for(int f = 0; f < this.numFiles; ++f) {
      for(int i = 0; i < this.numItemsPerFile; ++i) {
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
    for(int i = 0; i < this.numFiles; ++i) {
      index.removeFile(files[i]);
    }
  }
}
