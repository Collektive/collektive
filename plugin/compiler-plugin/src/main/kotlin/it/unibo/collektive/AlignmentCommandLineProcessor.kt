package it.unibo.collektive

import it.unibo.collektive.alignment.AlignmentMode
import it.unibo.collektive.alignment.DebugMode
import it.unibo.collektive.alignment.PrototypeMode
import it.unibo.collektive.alignment.ReleaseMode
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
    companion object {
        private const val OPTION_ENABLED = "enabled"
        private const val OPTION_ALIGNMENT_MODE = "alignmentMode"
        val ARG_ENABLED = CompilerConfigurationKey<Boolean>(OPTION_ENABLED)
        val PATH_ALIGNMENT_MODE = CompilerConfigurationKey<AlignmentMode>(OPTION_ALIGNMENT_MODE)
    }

    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = OPTION_ENABLED,
            valueDescription = "bool <true | false>",
            description = "If the alignment plugin should be applied",
            required = false,
        ),
        CliOption(
            optionName = OPTION_ALIGNMENT_MODE,
            valueDescription = "alignment mode <debug | prototype | release>",
            description = "The alignment mode",
            required = false,
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        return when (option.optionName) {
            OPTION_ENABLED -> configuration.put(ARG_ENABLED, value.toBoolean())
            OPTION_ALIGNMENT_MODE -> configuration.put(PATH_ALIGNMENT_MODE, parseAlignmentMode(value))
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }

    private fun parseAlignmentMode(value: String): AlignmentMode {
        return when (value) {
            "debug", "Debug" -> DebugMode
            "prototype", "Prototype" -> PrototypeMode
            "release", "Release" -> ReleaseMode
            else -> throw IllegalArgumentException("Unexpected alignment mode: $value")
        }
    }
}
