package com.yan.asmlocal;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

public final class AddTryCatchMethodAdapter extends LocalVariablesSorter implements Opcodes {
    private String classNamePath;
    private String className;
    private String superClassName;
    private String methodDes;
    private String methodName;
    private AsmLocalExtension asmLocalExtension;

    public AddTryCatchMethodAdapter(String className, String superClassName, String methodName, int access,
                                 String desc,
                                 MethodVisitor mv, AsmLocalExtension asmLocalExtension) {
        super(Opcodes.ASM5, access, desc, mv);
        this.classNamePath = className.replace(".", "/");
        this.className = className;
        this.superClassName = superClassName;
        this.methodName = methodName;
        this.methodDes = desc;
        this.asmLocalExtension = asmLocalExtension;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
    }

    Label l1;
    Label l2;

    @Override
    public void visitCode() {
        super.visitCode();
        KernelLog.info("onMethodEnter onMethodEnter onMethodEnter");
        Label l0 = new Label();
        l1 = new Label();
        l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
        mv.visitLabel(l0);
    }

    @Override
    public void visitInsn(int opcode) {
        if (((opcode >= IRETURN && opcode <= RETURN))) {
            mv.visitLabel(l1);
            Label l3 = new Label();
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" });
            mv.visitVarInsn(ASTORE, 1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitLabel(l3);
        }
        super.visitInsn(opcode);

    }
}
