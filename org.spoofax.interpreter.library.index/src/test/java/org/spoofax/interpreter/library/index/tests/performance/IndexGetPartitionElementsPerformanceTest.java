package org.spoofax.interpreter.library.index.tests.performance;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.spoofax.interpreter.library.index.IndexEntry;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 3, callgc = true, clock = Clock.CPU_TIME)
@RunWith(value = Parameterized.class)
public class IndexGetPartitionElementsPerformanceTest extends IndexPerformanceTest {
	@Rule
	public BenchmarkRule benchmarkRun;

	public IndexGetPartitionElementsPerformanceTest(int numItems, int numFiles) {
		super(numItems, numFiles);

		try {
			benchmarkRun =
				new BenchmarkRule(new CSVResultsConsumer((this.numItems * 5) + "," + this.numFiles, new FileWriter(
					"get-partition-elems_" + this.numFiles + ".csv", true)));
		} catch(IOException e) {
			e.printStackTrace();
		}

		index.reset();

		for(int i = 0; i < this.numItems; ++i) {
			collect(def1, getNextFile());
			collect(def2, getNextFile());
			collect(def3, getNextFile());
			collect(use1, getNextFile());
			collect(type1, getNextFile());
		}
	}

	@Test
	public void getPartitionElements() {
		Iterable<IndexEntry> ret;
		for(int i = 0; i < this.numFiles; ++i) {
			ret = index.getInSource(files[i]);
			ret.iterator();
		}
	}
}
