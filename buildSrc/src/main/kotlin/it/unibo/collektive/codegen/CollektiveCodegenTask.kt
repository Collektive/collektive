package it.unibo.collektive.codegen

import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.toolchain.JavaLanguageVersion
import java.io.File
import java.net.URLClassLoader

abstract class CollektiveCodegenTask : JavaExec() {
    @OutputDirectory
    lateinit var outputDir: File

    init {
        this.javaLauncher.set(
            javaToolchainService.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(8))
            }
        )
        mainClass.set(FieldedMembersGenerator::class.qualifiedName)
    }

    @TaskAction
    override fun exec() {
        args(outputDir.absolutePath)
        val classpath: Array<String> = sequenceOf(this::class.java.classLoader)
            //generateSequence(this::class.java.classLoader) { it.parent }
            .filterIsInstance<URLClassLoader>()
            .flatMap { it.urLs.asSequence() }
            .orEmpty()
            .mapNotNull { it.file.takeUnless { it.isNullOrBlank() } }
            .toList()
            .toTypedArray()
        check(classpath.isNotEmpty()) {
            "Classpath detection for the Collektive code generation task failed."
        }
        classpath(*classpath)
        outputDir.mkdirs()
        super.exec()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val outputDir = args[0]
            FieldedMembersGenerator.generateFieldFunctionsForTypes(FieldedMembersGenerator.baseTargetTypes).forEach {
                it.writeTo(File(outputDir))
            }
        }
    }
}
