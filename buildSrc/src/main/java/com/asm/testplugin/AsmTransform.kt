package com.asm.testplugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import groovyjarjarasm.asm.ClassReader
import groovyjarjarasm.asm.ClassWriter
import com.android.utils.FileUtils
import org.gradle.api.Project
import java.io.FileOutputStream


class AsmTransform(project: Project): Transform() {
    override fun getName(): String {
        return "AsmTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
       return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.PROJECT_ONLY
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        println("222222222222")
        val inputs = transformInvocation?.inputs
        val out = transformInvocation?.outputProvider

        inputs?.forEach { transformInput ->

            //项目目录
            transformInput.directoryInputs.forEach { directoryInput ->
                if (directoryInput.file.isDirectory) {
                    FileUtils.getAllFiles(directoryInput.file).forEach {
                        val file = it
                        val name = file.name
                        if (name.endsWith(".class") && name != "R.class" && !name.startsWith("R\$") && name != "BuildConfig.class") {
                            val classPath = file.absolutePath
                            val cr = ClassReader(file.readBytes())
                            val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                            val visitor =null
                            cr.accept(visitor, ClassReader.EXPAND_FRAMES)

                            val byte = cw.toByteArray();
                            val fos = FileOutputStream(classPath)
                            fos.write(byte)
                            fos.close()
                        }
                    }
                }

                val dest = out?.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )

                FileUtils.copyDirectoryToDirectory(directoryInput.file, dest)

            }


            //jar包
            transformInput.jarInputs.forEach {
                val dest = out?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )

                FileUtils.copyFile(it.file, dest)
            }

        }

    }

}

