package com.a.plugin.asmplugin

import com.a.plugin.ConvertUtils
import com.a.plugin.privacycheck.PrivacyCheckRob
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.bytecode.AccessFlag
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class AsmTransform1 extends Transform {

    ClassPool classPool = ClassPool.default
    Project project

    AsmTransform1(project) {
        this.project = project
    }

    @Override
    String getName() {
        return "AsmTransform1"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws Exception {
        super.transform(transformInvocation)

        println "----------AsmTransform1 start----------"

        project.android.bootClasspath.each {
            classPool.appendClassPath(it.absolutePath)
        }

        //所有的class经过修改后汇集到这个jar文件中
        File jarFile = generateAllClassOutJarFile(transformInvocation)

        //汇集所有class，包括我们编写的java代码和第三方jar中的class
        def ctClasses = ConvertUtils.toCtClasses(transformInvocation.inputs, classPool)

        insertCode(ctClasses,jarFile)
        //修改并打包进jarFile
//        PrivacyCheckRob.insertCode(ctClasses, jarFile)


//        Collection<TransformInput> inputs = transformInvocation.inputs
//        TransformOutputProvider out = transformInvocation.outputProvider
//
//        inputs.each { transformInput ->
//
//            //项目目录
//            transformInput.directoryInputs.each { directoryInput ->
//                if (directoryInput.file.isDirectory()) {
//                    FileUtils.getAllFiles(directoryInput.file).forEach {
//                        File file = it
//                        String name = file.name
//                        if (name.endsWith(".class") && name != "R.class" && !name.startsWith("R\$") && name != "BuildConfig.class") {
//                            String classPath = file.absolutePath
//                            ClassReader cr = new ClassReader(file.readBytes())
//                            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
//                            AddLogClassVisitor visitor = new AddLogClassVisitor(cw)
//                            cr.accept(visitor, ClassReader.EXPAND_FRAMES)
//
//                            byte[] bytes = cw.toByteArray();
//                            FileOutputStream fos = new FileOutputStream(classPath)
//                            fos.write(bytes)
//                            fos.close()
//                        }
//                    }
//                }
//
//                File dest = out.getContentLocation(
//                        directoryInput.name,
//                        directoryInput.contentTypes,
//                        directoryInput.scopes,
//                        Format.DIRECTORY
//                )
//
//                FileUtils.copyDirectoryToDirectory(directoryInput.file, dest)
//
//            }
//
//
//            //jar包
//            transformInput.jarInputs.forEach {
//                File dest = out.getContentLocation(
//                        it.name,
//                        it.contentTypes,
//                        it.scopes,
//                        Format.JAR
//                )
//
//                FileUtils.copyFile(it.file, dest)
//            }
//        }
        println "----------AsmTransform1 end----------"

//        throw new NullPointerException(("hahahahahahaha"))
    }

    private File generateAllClassOutJarFile(TransformInvocation transformInvocation) {
        File jarFile = transformInvocation.outputProvider.getContentLocation(
                "main", getOutputTypes(), getScopes(), Format.JAR);
        println("jarFile:" + jarFile.absolutePath)
        if (!jarFile.getParentFile().exists()) {
            jarFile.getParentFile().mkdirs();
        }
        if (jarFile.exists()) {
            jarFile.delete();
        }
        return jarFile
    }

    protected void insertCode(List<CtClass> box, File jarFile) throws IOException, CannotCompileException {
        ZipOutputStream outStream = new JarOutputStream(new FileOutputStream(jarFile));
        //get every class in the box ,ready to insert code
        for (CtClass ctClass : box) {
            //change modifier to public ,so all the class in the apk will be public ,you will be able to access it in the patch
//            ctClass.setModifiers(AccessFlag.setPublic(ctClass.getModifiers()));
            if (isNeedInsertClass(ctClass.getName()) && !(ctClass.isInterface() || ctClass.getDeclaredMethods().length < 1)) {
//                only insert code into specific classes
                zipFile(transformCode(ctClass.toBytecode(), ctClass.getName().replaceAll("\\.", "/")), outStream, ctClass.getName().replaceAll("\\.", "/") + ".class");
            } else {
                zipFile(ctClass.toBytecode(), outStream, ctClass.getName().replaceAll("\\.", "/") + ".class");

            }
            ctClass.defrost();
        }
        outStream.close();
    }

    byte[] transformCode(byte[] b1, String className) throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassReader cr = new ClassReader(b1);
        ClassNode classNode = new ClassNode();
        Map<String, Boolean> methodInstructionTypeMap = new HashMap<>();
        cr.accept(classNode, 0);
        final List<MethodNode> methods = classNode.methods;
        for (MethodNode m : methods) {
            InsnList inList = m.instructions;
            boolean isMethodInvoke = false;
            for (int i = 0; i < inList.size(); i++) {
                if (inList.get(i).getType() == AbstractInsnNode.METHOD_INSN) {
                    isMethodInvoke = true;
                }
            }
            methodInstructionTypeMap.put(m.name + m.desc, isMethodInvoke);
        }
        AddLogClassVisitor insertMethodBodyAdapter = new AddLogClassVisitor(cw)
        cr.accept(insertMethodBodyAdapter, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    protected void zipFile(byte[] classBytesArray, ZipOutputStream zos, String entryName) {
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

    boolean isNeedInsertClass(String className){
        return true;
    }
}
