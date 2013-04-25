package org.spoofax.interpreter.library.index.tests.performance;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 3, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class IndexAddPerformanceTest extends IndexPerformanceTest {
    @Rule
    public BenchmarkRule benchmarkRun;

    public IndexAddPerformanceTest(int numItems, int numFiles, boolean startTransaction) {
        super(numItems, numFiles, startTransaction);
        
        try {
            benchmarkRun =
                new BenchmarkRule(new CSVResultsConsumer((this.numItems * 5) + "," + this.numFiles, new FileWriter(
                    "add_" + this.numFiles + "_" + indexTypeString() + ".csv", true)));
        } catch(IOException e) {
            e.printStackTrace();
        }

        index.clearAll();
    }

    @Test
    public void add() {
        startTransaction();
        
        for(int i = 0; i < numItems; ++i) {
            index.add(def1, getNextFile());
            index.add(def2, getNextFile());
            index.add(def3, getNextFile());
            index.add(use1, getNextFile());
            index.add(type1, getNextFile());
        }
        
        endTransaction();
    }
}
