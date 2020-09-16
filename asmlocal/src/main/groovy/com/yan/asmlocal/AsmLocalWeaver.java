package com.yan.asmlocal;

import com.quinn.hunter.transform.asm.BaseWeaver;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public final class AsmLocalWeaver extends BaseWeaver {

  private AsmLocalExtension asmLocalExtension;

  public AsmLocalWeaver(AsmLocalExtension asmLocalExtension) {
    this.asmLocalExtension = asmLocalExtension;
  }

  @Override
  public boolean isWeavableClass(String fullQualifiedClassName) {
    //过滤
    return super.isWeavableClass(fullQualifiedClassName);
  }

  @Override
  protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
    return new AsmLocalClassAdapter(classWriter, asmLocalExtension);
  }
}
