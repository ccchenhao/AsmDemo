package com.a.plugin.asmplugin


import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "this is AsmPlugin plugin"
        project.android.registerTransform(new AsmTransform1(project))
    }

}
