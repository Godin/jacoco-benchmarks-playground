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

import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.OfflineInstrumentationAccessGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
public class InstrumentationBenchmark {

  @Benchmark
  public int guava() throws IOException {
    // 2.2M
    return instrument("target/projects/guava.jar");
  }

  @Benchmark
  public int elasticsearch() throws IOException {
    // 13M
    return instrument("target/projects/elasticsearch.jar");
  }

  @Benchmark
  public int rt() throws IOException {
    // 60M
    return instrument(System.getProperty("java.home") + "/lib/rt.jar");
  }

  private int instrument(String jar) throws IOException {
    Instrumenter instrumenter = new Instrumenter(new OfflineInstrumentationAccessGenerator());
    FileInputStream inputStream = new FileInputStream(jar);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    instrumenter.instrumentAll(inputStream, outputStream, "");
    inputStream.close();
    outputStream.close();
    return outputStream.size();
  }

}
