/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler

import it.unibo.collektive.compiler.frontend.CollektiveFrontendExtensionRegistrar
import it.unibo.collektive.compiler.utils.logging.info
import it.unibo.collektive.compiler.utils.logging.strongWarning
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

/**
 * The component registrar registers the alignment generation extension if
 * the compiler plugin is enabled.
 */
@OptIn(ExperimentalCompilerApi::class)
class AlignmentComponentRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        if (configuration.get(AlignmentCommandLineProcessor.ARG_ENABLED) != false) {
            IrGenerationExtension.registerExtension(AlignmentIrGenerationExtension(logger))
            FirExtensionRegistrarAdapter.registerExtension(CollektiveFrontendExtensionRegistrar())
        }
    }

    /**
     * Ensures that a specific configuration key in the CompilerConfiguration is set to the required value.
     * If the key is not present or its value does not match the expected value, the configuration is
     * updated (if not finalized), or an error is raised based on specified conditions.
     *
     * @param key The configuration key to be checked or updated.
     * @param value The value that must be associated with the specified key.
     * @param logger A MessageCollector used for logging warnings or informational messages.
     * @param messageOnError A lambda function that provides a custom error message given the configuration key.
     */
    fun <T> CompilerConfiguration.requireConfiguration(
        key: CompilerConfigurationKey<T>,
        value: T,
        logger: MessageCollector,
        messageOnError: (CompilerConfigurationKey<T>) -> String,
    ) {
        val returnedValue: T? = get(key)
        if (returnedValue == null) {
            when {
                isReadOnly ->
                    logger.strongWarning(messageOnError(key) + " The compiler configuration has been finalized.")

                else -> {
                    requireNotNull(value) { "The value of the key '$key' cannot be null." }
                    put(key, value).also {
                        logger.info(
                            """
                            Implicitly enabled '$key' setting the value '$value'.
                            It is required by the Collektive compiler plugin.
                            """.trimIndent(),
                        )
                    }
                }
            }
        }
        if (returnedValue != value) {
            error(messageOnError(key))
        }
    }
}
