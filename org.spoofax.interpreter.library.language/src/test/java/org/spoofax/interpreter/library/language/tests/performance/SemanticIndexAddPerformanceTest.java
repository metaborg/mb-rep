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

@BenchmarkOptions(benchmarkRounds = 15, warmupRounds = 5, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class SemanticIndexAddPerformanceTest extends
    SemanticIndexPerformanceTest {
  @Rule
  public MethodRule benchmarkRun;
  
  private int numItems;
  private int numFiles;
  
  @Parameters
  public static List<Object[]> data() {
    Object[][] data = new Object[][] { 
        { 100   , 1    }
      , { 1000  , 1    }
      , { 10000 , 1    }
      , { 20000 , 1    }
      , { 50000 , 1    }
      , { 100000, 1    }
      , { 150000, 1    }
      , { 200000, 1    }
      , { 100   , 10   }
      , { 1000  , 10   }
      , { 10000 , 10   }
      , { 20000 , 10   }
      , { 50000 , 10   }
      , { 100000, 10   }
      , { 150000, 10   }
      , { 200000, 10   }
      , { 100   , 100  }
      , { 1000  , 100  }
      , { 10000 , 100  }
      , { 20000 , 100  }
      , { 50000 , 100  }
      , { 100000, 100  }
      , { 150000, 100  }
      , { 200000, 100  }
      , { 100   , 500  }
      , { 1000  , 500  }
      , { 10000 , 500  }
      , { 20000 , 500  }
      , { 50000 , 500  }
      , { 100000, 500  }
      , { 150000, 500  }
      , { 200000, 500  }
      , { 100   , 1000 }
      , { 1000  , 1000 }
      , { 10000 , 1000 }
      , { 20000 , 1000 }
      , { 50000 , 1000 }
      , { 100000, 1000 }
      , { 150000, 1000 }
      , { 200000, 1000 }
      };
    return Arrays.asList(data);
  }
  
  public SemanticIndexAddPerformanceTest(int numItems, int numFiles) {
    this.numItems = numItems;
    this.numFiles = numFiles;
    
    try {
      benchmarkRun = new BenchmarkRule(new CSVResultsConsumer(
          (this.numItems * 5) + "," + this.numFiles, new FileWriter("add.csv", true)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    index.clear();
  }

  @Test
  public void add() {
    for (int i = 0; i < numItems; ++i) {
      index.add(def1, getFile(numFiles));
      index.add(def2, getFile(numFiles));
      index.add(def3, getFile(numFiles));
      index.add(use1, getFile(numFiles));
      index.add(type1, getFile(numFiles));
    }
  }
}
