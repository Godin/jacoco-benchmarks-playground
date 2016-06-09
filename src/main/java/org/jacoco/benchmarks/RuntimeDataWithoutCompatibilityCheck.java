package org.jacoco.benchmarks;

import org.jacoco.core.runtime.RuntimeData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeDataWithoutCompatibilityCheck extends RuntimeData {

  private volatile boolean[] probes = new boolean[2];

  private Map<Long, boolean[]> m = new ConcurrentHashMap<Long, boolean[]>();

  public RuntimeDataWithoutCompatibilityCheck() {
    m.put(1L, new boolean[2]);
  }

  @Override
  public boolean equals(Object args) {
    if (args instanceof Object[]) {
      Object[] a = (Object[]) args;
      final long classId = (Long) a[0];
      a[0] = m.get(classId);
      return false;
    }
    return super.equals(args);
  }

}
