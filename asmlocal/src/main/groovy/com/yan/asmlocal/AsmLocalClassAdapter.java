package com.yan.asmlocal;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class AsmLocalClassAdapter extends ClassVisitor {

  private String className;
  private String superClassName;
  private AsmLocalExtension asmLocalExtension;

  AsmLocalClassAdapter(final ClassVisitor cv, AsmLocalExtension asmLocalExtension) {
    super(Opcodes.ASM6, cv);
    this.asmLocalExtension = asmLocalExtension;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    super.visit(version, access, name, signature, superName, interfaces);
    if (name != null) {
      this.className = name.replace("/", ".");
    }
    if (superName != null) {
      this.superClassName = superName.replace("/", ".");
    }
  }


  @Override
  public MethodVisitor visitMethod(final int access, final String name,
      final String desc, final String signature, final String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    //KernelLog.info("visitMethod "
    //    + access
    //    + "  "
    //    + name
    //    + "  "
    //    + desc
    //    + "  "
    //    + signature
    //    + "   true true true ");

    if (ClickDoubleWrapAdapter.intercept(className, superClassName, access, name, desc, signature,
        exceptions)) {
      return mv == null ? null
          : new ClickDoubleWrapAdapter(className, superClassName, name, access, desc, mv,
              asmLocalExtension);
    }

    if (className.contains("io.flutter.embedding.android.FlutterTextureView")
        && name.contains("disconnectSurfaceFromRenderer")
    ) {

      return mv == null ? null
          //: new AddTryCatchAdviceAdapter(Opcodes.ASM6, mv, access, name, desc);
          : new AddTryCatchMethodAdapter(className, superClassName, name, access, desc, mv,
              asmLocalExtension);
    } else {
      return mv;
    }
  }
}