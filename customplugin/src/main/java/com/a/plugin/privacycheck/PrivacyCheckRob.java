package com.a.plugin.privacycheck;


import com.a.plugin.privacycheck.asm.PrivacyClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class PrivacyCheckRob {
    public static void insertCode(List<CtClass> ctClasses, File jarFile) throws Exception {
        long startTime = System.currentTimeMillis();
        if (PrivacyConfig.useAsm) {
            insertCodeByAsm(ctClasses, jarFile);
        } else {
            insertCodeByJavassist(ctClasses, jarFile);
        }
        float cost = (System.currentTimeMillis() - startTime) / 1000.0f;
        System.out.println((PrivacyConfig.useAsm ? "asm" : "javassist") + " insertCode cost " + cost + " second");
    }

    public static void insertCodeByJavassist(List<CtClass> ctClasses, File jarFile) throws Exception {
        ZipOutputStream outStream = new JarOutputStream(new FileOutputStream(jarFile));
        for (CtClass ctClass : ctClasses) {
            if (ctClass.isFrozen()) ctClass.defrost();
            if (!ctClass.isFrozen() && !PrivacyConfig.ignoreClasses.contains(ctClass.getName())) {
//            if (!ctClass.isFrozen()&&!ctClass.getName().equals("com.a.privacychecker.MainApp")) {
                for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                    ctMethod.instrument(new ExprEditor() {
                        @Override
                        public void edit(MethodCall m) throws CannotCompileException {
                            String mLongName = m.getClassName() + "." + m.getMethodName();
                            if (PrivacyConfig.methodHookValueSet.contains(mLongName) && !skipPackage(ctMethod.getLongName())) {
                                systemOutPrintln(mLongName, m.getLineNumber(), ctMethod);
//                                InjectAddLog.execute(m);
                                InjectHookReturnValue.execute(m);
//                                InjectMethodProxy.execute(m);
                            }
                        }

                        @Override
                        public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                            String mLongName = fieldAccess.getClassName() + "." + fieldAccess.getFieldName();
                            if (PrivacyConfig.fieldHookValueSet.contains(mLongName) && fieldAccess.isReader()) {
                                systemOutPrintln(mLongName, fieldAccess.getLineNumber(), ctMethod);
                                InjectHookReturnValue.execute(fieldAccess);
                            }
                        }

                        private void systemOutPrintln(String mLongName, int lineNumber, CtMethod ctMethod) {
                            String sb = "\n========" +
                                    "\ncall: " + mLongName +
                                    "\n  at: " + ctMethod.getLongName() + "(" + ctMethod.getDeclaringClass().getSimpleName() + ".java:" + lineNumber + ")";
                            System.out.println(sb);
                        }
                    });
                }

            }
            zipFile(ctClass.toBytecode(), outStream, ctClass.getName().replaceAll("\\.", "/") + ".class");
        }
        outStream.close();
    }

    public static boolean skipPackage(String ctMethodLongName) {
        return ctMethodLongName.startsWith("androidx")
                || ctMethodLongName.startsWith("io.flutter")
                || ctMethodLongName.startsWith("com.ta.a.d.e")
                || ctMethodLongName.startsWith("com.ta.utdid2.device.c")
                || ctMethodLongName.startsWith("com.idlefish");
    }

    public static void zipFile(byte[] classBytesArray, ZipOutputStream zos, String entryName) {
        try {
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            zos.write(classBytesArray, 0, classBytesArray.length);
            zos.closeEntry();
            zos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertCodeByAsm(List<CtClass> box, File jarFile) throws IOException, CannotCompileException {
        ZipOutputStream outStream = new JarOutputStream(new FileOutputStream(jarFile));
        for (CtClass ctClass : box) {
            if (!PrivacyCheckRob.skipPackage(ctClass.getName()) && !PrivacyConfig.ignoreClasses.contains(ctClass.getName()) && !(ctClass.isInterface() || ctClass.getDeclaredMethods().length < 1)) {
                zipFile(asmTransformCode(ctClass.toBytecode()), outStream, ctClass.getName().replaceAll("\\.", "/") + ".class");
            } else {
                zipFile(ctClass.toBytecode(), outStream, ctClass.getName().replaceAll("\\.", "/") + ".class");

            }
//            ctClass.defrost();
        }
        outStream.close();
    }

    private static byte[] asmTransformCode(byte[] b1) throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassReader cr = new ClassReader(b1);
        PrivacyClassVisitor insertMethodBodyAdapter = new PrivacyClassVisitor(cw);
        cr.accept(insertMethodBodyAdapter, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

}
