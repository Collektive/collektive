/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

/**
 * The command line processor is used to define the expected command line
 * option, which enable or disable the plugin that is responsible for the alignment.
 */
@OptIn(ExperimentalCompilerApi::class)
class AlignmentCommandLineProcessor : CommandLineProcessor {
    /**
     * The companion object is used to define the keys used by the compiler
     * to enable or disable the plugin, and other future options.
     */
    companion object {
        private const val OPTION_ENABLED = "collektiveEnabled"

        /**
         * The key used by the compiler to enable or disable the plugin.
         */
        val ARG_ENABLED = CompilerConfigurationKey<Boolean>(OPTION_ENABLED)

        /**
         * The plugin id used to identify the plugin in the compiler.
         */
        const val PLUGIN_ID = BuildConfig.KOTLIN_PLUGIN_ID
    }

    override val pluginId: String = PLUGIN_ID

    override val pluginOptions: Collection<CliOption> =
        listOf(
            CliOption(
                optionName = OPTION_ENABLED,
                valueDescription = "bool <true | false>",
                description = "If the alignment plugin should be applied",
                required = false,
            ),
        )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) =
        when (option.optionName) {
            OPTION_ENABLED -> configuration.put(ARG_ENABLED, value.toBoolean())
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
}
