package it.unibo.collektive

import it.unibo.collektive.codegen.baseTargetTypes
import it.unibo.collektive.codegen.generateFieldFunctionsForTypes
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CollektiveCodegenTask : DefaultTask() {
    @OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun generate() {
        outputDir.mkdirs()
        generateFieldFunctionsForTypes(baseTargetTypes).forEach {
            it.writeTo(outputDir)
        }
    }
}