package org.spoofax.interpreter.library.language.tests.performance;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
public class IndexPersistPerformanceTest extends IndexPerformanceTest {
    @Rule
    public MethodRule benchmarkRun;

    private int numItems;
    private int numFiles;

    public IndexPersistPerformanceTest(int numItems, int numFiles) {
        this.numItems = numItems;
        this.numFiles = numFiles;

        try {
            benchmarkRun =
                new BenchmarkRule(new CSVResultsConsumer((this.numItems * 5) + "," + this.numFiles, new FileWriter(
                    "persist_" + this.numFiles + ".csv", true)));
        } catch(IOException e) {
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
    public void persist() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("persist.dat", false));
        outputStream.writeObject(index.getAllEntries().toArray());
        outputStream.flush();
        outputStream.close();
    }
}
