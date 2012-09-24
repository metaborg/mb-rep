package org.spoofax.interpreter.library.language.tests.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.spoofax.interpreter.library.language.SemanticIndexEntry;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 15, warmupRounds = 5, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class SemanticIndexGetPerformanceTest extends
    SemanticIndexPerformanceTest {
  @Rule
  public MethodRule benchmarkRun;
  
  private static int NUM_GET = 100000;
  
  private int numItems;
  private int numFiles;

  @Parameters
  public static List<Object[]> data() {
    Object[][] data = new Object[][] { 
      { 100   , 1    }
    , { 1000  , 1    }
    , { 10000 , 1    }
    , { 100000, 1    }
    , { 200000, 1    }
    , { 100   , 10   }
    , { 1000  , 10   }
    , { 10000 , 10   }
    , { 100000, 10   }
    , { 200000, 10   }
    , { 100   , 100  }
    , { 1000  , 100  }
    , { 10000 , 100  }
    , { 100000, 100  }
    , { 200000, 100  }
    , { 100   , 500  }
    , { 1000  , 500  }
    , { 10000 , 500  }
    , { 100000, 500  }
    , { 200000, 500  }
    , { 100   , 1000 }
    , { 1000  , 1000 }
    , { 10000 , 1000 }
    , { 100000, 1000 }
    , { 200000, 1000 }
    };
    return Arrays.asList(data);
  }
  
  public SemanticIndexGetPerformanceTest(int numItems, int numFiles) {
    this.numItems = numItems;
    this.numFiles = numFiles;
    
    try {
      benchmarkRun = new BenchmarkRule(new CSVResultsConsumer(
          (this.numItems * 5) + "," + this.numFiles, new FileWriter("get.csv", true)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    index.clear();

    for(int i = 0; i < this.numItems; ++i) {
      index.add(def1, getFile(this.numFiles));
      index.add(def2, getFile(this.numFiles));
      index.add(def3, getFile(this.numFiles));
      index.add(use1, getFile(this.numFiles));
      index.add(type1, getFile(this.numFiles));
    }
  }

  @Test
  public void get() {
    @SuppressWarnings("unused")
    Collection<SemanticIndexEntry> ret;
    for(int i = 0; i < NUM_GET; ++i) {
      ret = index.getEntries(def1);
      ret = index.getEntries(def2);
      ret = index.getEntries(def3);
      ret = index.getEntries(use1);
      ret = index.getEntries(typeTemplate1);
    }
  }
}
