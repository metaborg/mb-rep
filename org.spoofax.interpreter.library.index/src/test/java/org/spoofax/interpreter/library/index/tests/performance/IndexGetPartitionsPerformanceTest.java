package org.spoofax.interpreter.library.index.tests.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.spoofax.interpreter.library.index.IndexPartition;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 3, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class IndexGetPartitionsPerformanceTest extends IndexPerformanceTest {
    @Rule
    public BenchmarkRule benchmarkRun;

    private static int NUM_GET = 200000;

    public IndexGetPartitionsPerformanceTest(int numItems, int numFiles, boolean startTransaction) {
        super(numItems, numFiles, startTransaction);

        try {
            benchmarkRun =
                new BenchmarkRule(new CSVResultsConsumer((this.numItems * 5) + "," + this.numFiles, new FileWriter(
                    "get-partitions_" + this.numFiles + "_" + indexTypeString() + ".csv", true)));
        } catch(IOException e) {
            e.printStackTrace();
        }

        index.clearAll();

        for(int i = 0; i < this.numItems; ++i) {
            index.add(def1, getNextFile());
            index.add(def2, getNextFile());
            index.add(def3, getNextFile());
            index.add(use1, getNextFile());
            index.add(type1, getNextFile());
        }
    }

    @Test
    public void getPartitions() {
        startTransaction();
        
        @SuppressWarnings("unused")
        Collection<IndexPartition> ret;
        for(int i = 0; i < NUM_GET; ++i) {
            ret = index.getAllPartitions();
        }
        
        endTransaction();
    }
}
