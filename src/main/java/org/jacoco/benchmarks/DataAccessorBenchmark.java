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
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@Fork(1)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public abstract class DataAccessorBenchmark {

  private DataAccessor dataAccessor;

  @Setup
  public void setup() throws Exception {
    dataAccessor = createDataAccessor();
  }

  protected abstract DataAccessor createDataAccessor() throws Exception;

  @Benchmark
  public boolean[] getData() {
    return dataAccessor.getData();
  }

  public static class NoOp extends DataAccessorBenchmark {
    @Override
    protected DataAccessor createDataAccessor() throws Exception {
      return null;
    }

    @Override
    public boolean[] getData() {
      return null;
    }
  }

  public static class DirectAccess extends DataAccessorBenchmark {
    @Override
    protected DataAccessor createDataAccessor() throws Exception {
      return new DataAccessor.Direct(new RuntimeData());
    }
  }

  public static class ModifiedSystemClass extends DataAccessorBenchmark {
    @Override
    protected DataAccessor createDataAccessor() throws Exception {
      return DataAccessor.generateFor(new ModifiedSystemClassRuntime(ModifiedClass.class, "accessField"));
    }

    public static class ModifiedClass {
      /**
       * This static member emulate the instrumented system class.
       */
      @SuppressWarnings("UnusedDeclaration")
      public static Object accessField;
    }
  }

  public static class URLStreamHandler extends DataAccessorBenchmark {
    @Override
    protected DataAccessor createDataAccessor() throws Exception {
      return DataAccessor.generateFor(new URLStreamHandlerRuntime());
    }
  }

  public static class SystemProperties extends DataAccessorBenchmark {
    @Override
    protected DataAccessor createDataAccessor() throws Exception {
      return DataAccessor.generateFor(new SystemPropertiesRuntime());
    }
  }

  public static class Logger extends DataAccessorBenchmark {
    @Override
    protected DataAccessor createDataAccessor() throws Exception {
      return DataAccessor.generateFor(new LoggerRuntime());
    }
  }

}
