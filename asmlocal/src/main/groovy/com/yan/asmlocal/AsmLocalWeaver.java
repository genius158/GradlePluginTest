package com.yan.asmlocal;

import com.quinn.hunter.transform.asm.BaseWeaver;
import groovy.util.AntBuilder;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.taskdefs.Ant;
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

  @Override public byte[] weaveSingleClassToByteArray(InputStream inputStream) throws IOException {
    return super.weaveSingleClassToByteArray(inputStream);
  }

  @Override
  protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
    return new AsmLocalClassAdapter(classWriter, asmLocalExtension);
  }
}
