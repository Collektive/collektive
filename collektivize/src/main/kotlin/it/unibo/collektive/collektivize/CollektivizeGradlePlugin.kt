/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize

import it.unibo.collektive.collektivize.FieldedMembersGenerator.baseExtensions
import it.unibo.collektive.collektivize.FieldedMembersGenerator.baseTargetTypes
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import java.io.File
import kotlin.reflect.KClass

/**
 * Collekivize Gradle plugin.
 */
open class CollektivizeGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create<CollektivizeExtension>("collektivize")
        target.tasks.register<CollektivizeTask>("collektivizeKotlinStdlib") {
            typesToField.set(extension.typesToField.get() + extension.extensionsToField.get())
            outputDirectory.set(extension.outputDirectory.get())
        }
    }
}

/**
 * Code generation task generating "fielded" version for each [typesToField] members.
 * The generated code is written to [outputDirectory].
 */
open class CollektivizeTask : DefaultTask() {
    /**
     * All the types that should be "fielded".
     */
    @Internal
    val typesToField: ListProperty<KClass<*>> = project.objects.listProperty()

    /**
     * Output directory for the generated code.
     */
    @OutputDirectory
    val outputDirectory: Property<File> = project.objects.property()

    /**
     * Code generation task.
     */
    @TaskAction
    fun collektivize() {
        val result =
            runCatching {
                FieldedMembersGenerator
                    .generateFieldFunctionsForTypes(typesToField.get().asSequence())
                    .forEach {
                        val generatedFile = it.writeTo(outputDirectory.get())
                        logger.debug("Generated file: ${generatedFile.absolutePath}")
                    }
            }
        when {
            result.isSuccess -> logger.lifecycle("Fielded members generated successfully.")
            result.isFailure -> logger.error("Fielded members generation failed.", result.exceptionOrNull())
        }
    }
}

/**
 * Extension DSL for the plugin.
 */
open class CollektivizeExtension(objects: ObjectFactory) {
    /**
     * The types that should be "fielded".
     */
    val typesToField: ListProperty<KClass<*>> =
        objects.listProperty<KClass<*>>().convention(baseTargetTypes.toList())

    /**
     * Base class to inspect for "fielding" extensions.
     */
    val extensionsToField: ListProperty<KClass<*>> =
        objects.listProperty<KClass<*>>().convention(baseExtensions.toList())

    /**
     * Output directory for the generated code.
     */
    val outputDirectory: Property<File> = objects.property<File>()
}
