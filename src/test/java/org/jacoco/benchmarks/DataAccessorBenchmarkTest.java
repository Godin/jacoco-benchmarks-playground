package org.jacoco.benchmarks;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DataAccessorBenchmarkTest {

  @Test
  public void test() throws Exception {
    DataAccessorBenchmark benchmark = new DataAccessorBenchmark();
    benchmark.setup();
    assertNotNull(benchmark.fast());
    benchmark.tearDown();
  }

}
