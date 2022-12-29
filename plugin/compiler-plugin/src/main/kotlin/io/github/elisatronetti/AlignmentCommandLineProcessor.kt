package io.github.elisatronetti

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import plugin.BuildConfig

/**
 * The command line processor is used to define the expected command line
 * option, which enable or disable the plugin that is responsible for the alignment.
 */
@AutoService(CommandLineProcessor::class)
class AlignmentCommandLineProcessor : CommandLineProcessor {
    companion object {
        private const val OPTION_ENABLED = "enabled"
        val ARG_ENABLED = CompilerConfigurationKey<Boolean>(OPTION_ENABLED)
    }

    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = OPTION_ENABLED,
            valueDescription = "bool <true | false>",
            description = "If the alignment plugin should be applied",
            required = false,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        return when (option.optionName) {
            OPTION_ENABLED -> configuration.put(ARG_ENABLED, value.toBoolean())
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}
