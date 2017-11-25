package com.tezm.wax

import org.gradle.api.Plugin
import org.gradle.api.Project

class WaxPlugin implements Plugin<Project>
{
    void apply(Project project)
    {
        project.afterEvaluate {
            project.android.applicationVariants.each { variant ->
                variant.javaCompiler.doLast {
                    project.javaexec {
                        classpath += variant.javaCompiler.classpath
                        classpath += project.files(variant.javaCompiler.destinationDir)
                        main = 'com.tezm.wax.Wax'
                        args variant.javaCompiler.destinationDir, project.buildDir
                    }
                }
            }
        }
    }
}
