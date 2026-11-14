/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler

import it.unibo.collektive.compiler.frontend.CollektiveFrontendExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.MessageCollectorAccess
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

/**
 * Registers the Collektive compiler plugin components with the Kotlin compiler.
 *
 * This includes both IR-based and FIR-based extensions, depending on plugin settings.
 */
@OptIn(ExperimentalCompilerApi::class)
class CollektiveCompilerPluginRegistrar : CompilerPluginRegistrar() {

    override val pluginId: String get() = BuildConfig.KOTLIN_PLUGIN_ID

    override val supportsK2: Boolean = true

    // CompilerConfiguration.reportLog only supports the LOGGING severity: the plugin needs the full message collector.
    @OptIn(MessageCollectorAccess::class)
    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger = configuration[CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE]
        if (configuration[CollektiveCommandLineProcessor.ARG_ENABLED] != false) {
            IrGenerationExtension.registerExtension(CollektiveIrGenerationExtension(logger))
            FirExtensionRegistrarAdapter.registerExtension(CollektiveFrontendExtensionRegistrar())
        }
    }
}
