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
import org.jacoco.core.runtime.RuntimeData;
import org.jacoco.core.test.TargetLoader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public abstract class DataAccessor {

  private static final long CLASS_ID = 1;
  private static final String CLASS_NAME = "org/jacoco/test/targets/DataAccessor";
  private static final int PROBES_COUNT = 2;

  public abstract boolean[] getData();

  public static DataAccessor generateFor(IRuntime runtime) throws Exception {
    runtime.startup(new RuntimeData());

    final ClassWriter writer = new ClassWriter(0);
    writer.visit(
      Opcodes.V1_5, Opcodes.ACC_PUBLIC, CLASS_NAME, null,
      Type.getInternalName(DataAccessor.class),
      new String[] { }
    );

    // Constructor
    GeneratorAdapter gen = new GeneratorAdapter(
      writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, new String[0]),
      Opcodes.ACC_PUBLIC, "<init>", "()V"
    );
    gen.visitCode();
    gen.loadThis();
    gen.invokeConstructor(Type.getType(DataAccessor.class), new Method("<init>", "()V"));
    gen.returnValue();
    gen.visitMaxs(1, 0);
    gen.visitEnd();

    // getData()
    gen = new GeneratorAdapter(
      writer.visitMethod(Opcodes.ACC_PUBLIC, "getData", "()[Z", null, new String[0]),
      Opcodes.ACC_PUBLIC, "getData", "()[Z"
    );
    gen.visitCode();
    final int size = runtime.generateDataAccessor(CLASS_ID, CLASS_NAME, PROBES_COUNT, gen);
    gen.returnValue();
    gen.visitMaxs(size + 1, 0);
    gen.visitEnd();

    writer.visitEnd();

    final TargetLoader loader = new TargetLoader();
    return (DataAccessor) loader.add(CLASS_NAME.replace('/', '.'), writer.toByteArray()).newInstance();
  }

  public static class Direct extends DataAccessor {
    private final RuntimeData runtimeData;

    public Direct(RuntimeData runtimeData) {
      this.runtimeData = runtimeData;
    }

    @Override
    public boolean[] getData() {
      return runtimeData.getExecutionData(CLASS_ID, CLASS_NAME, PROBES_COUNT).getProbes();
    }
  }

}
