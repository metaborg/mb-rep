package org.spoofax.interpreter.library.index.tests.performance;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 3, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class IndexClearPerformanceTest extends IndexPerformanceTest {
    @Rule
    public MethodRule benchmarkRun;

    private int fileIndexToClear;

    public IndexClearPerformanceTest(int numItemsPerFile, int numFiles, boolean startTransaction) {
        super(numItemsPerFile, numFiles, startTransaction);
        
        this.fileIndexToClear = (int) Math.floor((double) this.numFiles / 2);

        try {
            benchmarkRun =
                new BenchmarkRule(new CSVResultsConsumer((this.numItems * 5) + "," + this.numFiles, new FileWriter(
                    "clear_" + this.numFiles + "_" + indexTypeString() + ".csv", true)));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clear() {
        startTransaction();
        
        // Have to add items here, otherwise index will be empty after first round.
        // This results in the time taken to add entries being added.
        index.clearAll();
        for(int i = 0; i < this.numItems; ++i) {
            index.add(def1, getNextFile());
            index.add(def2, getNextFile());
            index.add(def3, getNextFile());
            index.add(use1, getNextFile());
            index.add(type1, getNextFile());
        }

        // Clear one file in the middle.
        index.clearPartition(files[fileIndexToClear]);
        
        endTransaction();
    }
}
