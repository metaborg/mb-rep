package org.spoofax.interpreter.library.language.tests.performance;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import com.carrotsearch.junitbenchmarks.IResultsConsumer;
import com.carrotsearch.junitbenchmarks.Result;

public class CSVResultsConsumer implements IResultsConsumer {
  private String prefixCSV;
  private Writer writer;
  
  public CSVResultsConsumer(String prefixCSV, Writer writer) {
    this.prefixCSV = prefixCSV;
    this.writer = writer;
  }

  public void accept(Result result) throws IOException {
    String line = String.format(Locale.ROOT, "%s,%f,%f%n", prefixCSV,
        result.roundAverage.avg, result.roundAverage.stddev);
    System.out.print(line);
    writer.write(line);
    writer.flush();
    writer.close();
  }
}
