/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler

import it.unibo.collektive.compiler.common.info
import it.unibo.collektive.compiler.common.strongWarning
import it.unibo.collektive.compiler.frontend.CollektiveFrontendExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

/**
 * Registers the Collektive compiler plugin components with the Kotlin compiler.
 *
 * This includes both IR-based and FIR-based extensions, depending on plugin settings.
 */
@OptIn(ExperimentalCompilerApi::class)
class CollektiveCompilerPluginRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        if (configuration.get(CollektiveCommandLineProcessor.ARG_ENABLED) != false) {
            IrGenerationExtension.registerExtension(CollektiveIrGenerationExtension(logger))
            FirExtensionRegistrarAdapter.registerExtension(CollektiveFrontendExtensionRegistrar())
        }
    }

    /**
     * Ensures that a [CompilerConfigurationKey] has a specific required value.
     *
     * If the key is absent:
     * - If the configuration is mutable, it sets the value and logs an info message.
     * - If the configuration is finalized, it logs a strong warning.
     *
     * If the key is present but holds a different value, an error is thrown.
     *
     * @param key The configuration key to validate or set.
     * @param value The required value for the key.
     * @param logger A [MessageCollector] used for logging diagnostics.
     * @param messageOnError Provides a custom error message if the requirement is violated.
     * @throws IllegalStateException if the key is present with a conflicting value.
     */
    fun <T> CompilerConfiguration.requireConfiguration(
        key: CompilerConfigurationKey<T>,
        value: T,
        logger: MessageCollector,
        messageOnError: (CompilerConfigurationKey<T>) -> String,
    ) {
        val returnedValue: T? = get(key)
        if (returnedValue == null) {
            if (isReadOnly) {
                logger.strongWarning(messageOnError(key) + " The compiler configuration has been finalized.")
            } else {
                requireNotNull(value) { "The value for key '$key' cannot be null." }
                put(key, value).also {
                    logger.info(
                        """
                        Implicitly enabled '$key' by setting its value to '$value'.
                        This setting is required by the Collektive compiler plugin.
                        """.trimIndent(),
                    )
                }
            }
        } else if (returnedValue != value) {
            error(messageOnError(key))
        }
    }
}
