package org.spoofax.interpreter.library.language.tests.performance;

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

    private int numItems;
    private int numFiles;
    private int fileIndexToClear;

    public IndexClearPerformanceTest(int numItemsPerFile, int numFiles) {
        this.numItems = numItemsPerFile;
        this.numFiles = numFiles;
        this.fileIndexToClear = (int) Math.floor((double) this.numFiles / 2);

        try {
            benchmarkRun =
                new BenchmarkRule(new CSVResultsConsumer((this.numItems * 5) + "," + this.numFiles, new FileWriter(
                    "clear_" + this.numFiles + ".csv", true)));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clear() {
        // Have to add items here, otherwise index will be empty after first round.
        // This results in the time taken to add entries being added.
        index.clear();
        for(int i = 0; i < this.numItems; ++i) {
            index.add(def1, getFile(this.numFiles));
            index.add(def2, getFile(this.numFiles));
            index.add(def3, getFile(this.numFiles));
            index.add(use1, getFile(this.numFiles));
            index.add(type1, getFile(this.numFiles));
        }

        // Clear one file in the middle.
        index.removePartition(files[fileIndexToClear]);
    }
}
