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

public class InstrumentationTest {

  @Test
  public void measure() throws RunnerException {
    Options options = new OptionsBuilder()
      .verbosity(VerboseMode.SILENT)
      .include(InstrumentationBenchmark.class.getCanonicalName())
      .build();

    Collection<RunResult> results = new Runner(options).run();
    for (RunResult r : results) {
      String name = simpleName(r.getParams().getBenchmark());
      double score = r.getPrimaryResult().getScore();
      double scoreError = r.getPrimaryResult().getStatistics().getMeanErrorAt(0.99);
      String scoreUnit = r.getPrimaryResult().getScoreUnit();
      System.out.printf("%15s: %8.3f +- %3.3f %s%n", name, score, scoreError, scoreUnit);
    }
  }

  private static String simpleName(String qName) {
    int lastDot = qName.lastIndexOf('.');
    return qName.substring(lastDot + 1);
  }

}
