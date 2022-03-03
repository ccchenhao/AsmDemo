package com.a.plugin.asmplugin;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddLogClassVisitor extends ClassVisitor {

    public AddLogClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }


    private String className;

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
        System.out.println("className ="+className);

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (className.equals("com/example/testgradle/TestActivity")) {
            System.out.println("TestActivity method =" + name);
            if (className.equals("com/example/testgradle/TestActivity") && name != "<init>") {
                if (name.equals("onCreate")) {
                    System.out.println("准备插入TestActivity的onCreate");
                    return new AddLogMethodVisitor(methodVisitor);
                }
            }
        }
        return methodVisitor;
    }
}


