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

import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.ModifiedSystemClassRuntime;
import org.jacoco.core.runtime.RuntimeData;
import org.jacoco.core.runtime.SystemPropertiesRuntime;
import org.jacoco.core.runtime.URLStreamHandlerRuntime;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class DataAccessorBenchmark {

  private DataAccessor direct;

  private IRuntime fastRuntime = new ModifiedSystemClassRuntime(ModifiedClass1.class, "accessField");
  private DataAccessor fast;

  private IRuntime modifiedSystemClassRuntime = new ModifiedSystemClassRuntime(ModifiedClass2.class, "accessField");
  private DataAccessor modifiedSystemClass;

  private IRuntime systemPropertiesRuntime = new SystemPropertiesRuntime();
  private DataAccessor systemProperties;

  private IRuntime loggerRuntime = new LoggerRuntime();
  private DataAccessor logger;

  private IRuntime streamHandlerRuntime = new URLStreamHandlerRuntime();
  private DataAccessor streamHandler;

  @Setup
  public void setup() throws Exception {
    direct = new DataAccessor.Direct(new RuntimeData());

    modifiedSystemClassRuntime.startup(new RuntimeData());
    modifiedSystemClass = DataAccessor.createFor(modifiedSystemClassRuntime);

    fastRuntime.startup(new RuntimeDataWithoutCompatibilityCheck());
    fast = DataAccessor.createFor(fastRuntime);

    systemPropertiesRuntime.startup(new RuntimeData());
    systemProperties = DataAccessor.createFor(systemPropertiesRuntime);

    loggerRuntime.startup(new RuntimeData());
    logger = DataAccessor.createFor(loggerRuntime);

    streamHandlerRuntime.startup(new RuntimeData());
    streamHandler = DataAccessor.createFor(streamHandlerRuntime);
  }

  @TearDown
  public void tearDown() {
    systemPropertiesRuntime.shutdown();
    loggerRuntime.shutdown();
    streamHandlerRuntime.shutdown();
    modifiedSystemClassRuntime.shutdown();
  }

  public static class ModifiedClass1 {
    /**
     * This static member emulate the instrumented system class.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static Object accessField;
  }

  public static class ModifiedClass2 {
    /**
     * This static member emulate the instrumented system class.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static Object accessField;
  }

  @Benchmark
  public boolean[] noOp() {
    return null;
  }

  @Benchmark
  public boolean[] direct() {
    return direct.getData();
  }

  @Benchmark
  public boolean[] fast() {
    return fast.getData();
  }

  @Benchmark
  public boolean[] modifiedSystemClass() {
    return modifiedSystemClass.getData();
  }

//  @Benchmark
  public boolean[] systemProperties() {
    return systemProperties.getData();
  }

//  @Benchmark
  public boolean[] logger() {
    return logger.getData();
  }

//  @Benchmark
  public boolean[] streamHandler() {
    return streamHandler.getData();
  }

  public static void main(String[] args) throws RunnerException {
    System.out.println(System.getProperty("java.runtime.name") + ", " + System.getProperty("java.runtime.version"));
    System.out.println(System.getProperty("java.vm.name") + ", " + System.getProperty("java.vm.version"));
    System.out.println(System.getProperty("os.name") + ", " + System.getProperty("os.version") + ", " + System.getProperty("os.arch"));
    // (Godin): This value may change during a particular invocation of the virtual machine:
    System.out.println(Runtime.getRuntime().availableProcessors() + " available processors");

    Options options = new OptionsBuilder()
      .include(DataAccessorBenchmark.class.getName())
      .build();
    new Runner(options).run();
  }

}
