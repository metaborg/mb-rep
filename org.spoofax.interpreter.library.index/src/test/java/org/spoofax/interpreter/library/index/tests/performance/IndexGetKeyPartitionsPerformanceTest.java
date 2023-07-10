package org.spoofax.interpreter.library.index.tests.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.metaborg.util.iterators.Iterables2;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.Clock;

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
        Set<IStrategoTerm> sources = new HashSet<IStrategoTerm>();
		Iterables2.addAll(sources, index.getSourcesOf(def1));
		Iterables2.addAll(sources, index.getSourcesOf(def2));
		Iterables2.addAll(sources, index.getSourcesOf(def3));
		Iterables2.addAll(sources, index.getSourcesOf(use1));
		Iterables2.addAll(sources, index.getSourcesOf(typeTemplate1));
	}
}
