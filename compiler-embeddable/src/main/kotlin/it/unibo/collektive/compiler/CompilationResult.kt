package it.unibo.collektive.compiler

import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.config.JVMConfigurationKeys.OUTPUT_DIRECTORY
import java.io.File

/**
 * Get the output directory for the KotlinJVM compiler from the configuration.
 */
fun GenerationState.jvmOutputDirectory(): File = checkNotNull(configuration.get(OUTPUT_DIRECTORY)) {
    "No output directory for KotlinJVM found in the configuration"
}
