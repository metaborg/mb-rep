package org.spoofax.interpreter.library.index.tests.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.spoofax.interpreter.library.index.IndexPartition;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;
import com.google.common.collect.Iterables;

@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 3, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class IndexGetKeyPartitionsPerformanceTest extends IndexPerformanceTest {
    @Rule
    public BenchmarkRule benchmarkRun;

    public IndexGetKeyPartitionsPerformanceTest(int numItems, int numFiles) {
        super(numItems, numFiles);

        try {
            benchmarkRun =
                new BenchmarkRule(new CSVResultsConsumer((this.numItems * 5) + "," + this.numFiles, new FileWriter(
                    "get-key-partitions_" + this.numFiles + ".csv", true)));
        } catch(IOException e) {
            e.printStackTrace();
        }

        index.reset();

        for(int i = 0; i < this.numItems; ++i) {
            add(def1, getNextFile());
            add(def2, getNextFile());
            add(def3, getNextFile());
            add(use1, getNextFile());
            add(type1, getNextFile());
        }
    }

    @Test
    public void getKeyPartitions() {
        HashSet<IndexPartition> files = new HashSet<IndexPartition>();
        Iterables.addAll(files, index.getSourcesOf(def1));
        Iterables.addAll(files, index.getSourcesOf(def2));
        Iterables.addAll(files, index.getSourcesOf(def3));
        Iterables.addAll(files, index.getSourcesOf(use1));
        Iterables.addAll(files, index.getSourcesOf(typeTemplate1));
    }
}
