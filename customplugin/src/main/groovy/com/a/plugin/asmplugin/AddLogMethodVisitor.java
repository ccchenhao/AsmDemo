package com.a.plugin.asmplugin;

import com.a.plugin.privacycheck.PrivacyConfig;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;


public class AddLogMethodVisitor extends MethodVisitor {


    public AddLogMethodVisitor(MethodVisitor methodVisitor) {
        super(Opcodes.ASM4, methodVisitor);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        System.out.println("Asm-visitVarInsn"+" var="+var);
        super.visitVarInsn(opcode, var);

    }

    /**】
     *
     * @param opcode
     * @param owner 所在class全限定名，比如com/example/testgradle/TestActivity
     * @param name  调用的方法名
     * @param descriptor 调用的方法的参数和返回值，比如findViewById是(I)Landroid/view/View
     * @param isInterface
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        System.out.println("Asm-visitMethodInsn"+" name="+name+" opcode="+opcode+" owner="+owner+" descriptor="+descriptor+" isInterface="+isInterface);
        String allName=owner+"/"+name;
        if (allName.equals("android/provider/Settings$Secure/getString")){
            System.out.println("testAsm修改代码start");
            //这样就不是通过代码动态控制，只能一开始通过配置修改
            boolean isAllow = PrivacyConfig.isAllow;
            mv.visitInsn(isAllow ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
            mv.visitLdcInsn(allName);
            //调用PrivacyProxy的privacyLog
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, AsmUtils.privacyProxy, "privacyLog", "(ZLjava/lang/String;)V", false);
            //调用Desc的getParams，将参数从descriptor转化成Class[]
            //如何获取参数的实参，
            mv.visitLdcInsn("222");
//            visitVarInsn(Opcodes.ALOAD,0);
//            visitVarInsn(Opcodes.ALOAD,1);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, AsmUtils.privacyProxy, "getString", "(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;", false);


//            mv.visitVarInsn(Opcodes.ALOAD,0);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/example/testgradle/TestActivity", "testAsm", "()Ljava/lang/String;", false);

//            //BaseApplication.agree入栈
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "com/example/testgradle/BaseApplication", "agree", "Z");
//            Label agreeLabel = new Label();
//            //IFLT是大于等于0，IFEQ是boolean
//            //判断if(BaseApplication.agree)
//            mv.visitJumpInsn(Opcodes.IFEQ, agreeLabel);
//            //注意按照惯性思维，调用之前的方法，我们可能会
//            // 这样visitMethodInsn(死循环)或super.visitMethodInsn()都是不行的
//            //asm就是根据我们现在写的插入，所以得通过mv.visitMethodInsn，
//            //但是注意了,这个testAsm是通过this调用的，还得alod this
//            mv.visitVarInsn(Opcodes.ALOAD,0);
//            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
//            //注意这个不能发这里，否则没有else，得放GOTO后面
//            //mv.visitLabel(agreeLabel);
//            Label notAgreeLabel = new Label();
//            mv.visitJumpInsn(Opcodes.GOTO, notAgreeLabel);
//            mv.visitLabel(agreeLabel);
//            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            mv.visitLdcInsn("not agree11");
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//            mv.visitLabel(notAgreeLabel);
//            mv.visitInsn(Opcodes.POP);
            System.out.println("testAsm修改代码end");
        }else{
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("AsmPlugin：开始插代码");
        mv.visitLdcInsn("chlog");
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("-------> onCreate : ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
        System.out.println("AsmPlugin：结束插代码");
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
//        ?????
        System.out.println("Asm-visitLocalVariable"+" name="+name+" descriptor="+descriptor+" signature="+signature+" start="+start+"end="+end+"index="+index);
    }
}
