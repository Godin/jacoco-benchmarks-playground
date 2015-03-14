/*******************************************************************************
 * Copyright (c) 2009, 2015 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.benchmarks;

import org.junit.Test;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Collection;

public class DataAccessorTest {

  @Test
  public void measure() throws RunnerException {
    System.out.println(System.getProperty("java.runtime.name") + ", " + System.getProperty("java.runtime.version"));
    System.out.println(System.getProperty("java.vm.name") + ", " + System.getProperty("java.vm.version"));
    System.out.println(System.getProperty("os.name") + ", " + System.getProperty("os.version") + ", " + System.getProperty("os.arch"));
    // (Godin): This value may change during a particular invocation of the virtual machine:
    System.out.println(Runtime.getRuntime().availableProcessors() + " available processors");

    runWith(1);
    runWith(2);
    runWith(4);
  }

  private static void runWith(int threads) throws RunnerException {
    System.out.println();
    System.out.println("Running with " + threads + " threads:");

    Options options = new OptionsBuilder()
      .threads(threads)
      .verbosity(VerboseMode.SILENT)
      .include(DataAccessorBenchmark.NoOp.class.getCanonicalName())
      .include(DataAccessorBenchmark.DirectAccess.class.getCanonicalName())
      .include(DataAccessorBenchmark.ModifiedSystemClass.class.getCanonicalName())
      .include(DataAccessorBenchmark.URLStreamHandler.class.getCanonicalName())
      .include(DataAccessorBenchmark.SystemProperties.class.getCanonicalName())
      .build();

    Collection<RunResult> results = new Runner(options).run();
    for (RunResult r : results) {
      String name = simpleName(r.getParams().getBenchmark());
      double score = r.getPrimaryResult().getScore();
      double scoreError = r.getPrimaryResult().getStatistics().getMeanErrorAt(0.99);
      String scoreUnit = r.getPrimaryResult().getScoreUnit();
      System.out.printf("%20s: %8.3f +- %6.3f %s%n", name, score, scoreError, scoreUnit);
    }
  }

  private static String simpleName(String qName) {
    int lastDot = qName.lastIndexOf('.');
    return qName.substring(DataAccessorBenchmark.class.getCanonicalName().length() + 1, lastDot);
  }

}
