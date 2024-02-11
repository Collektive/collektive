package it.unibo.collektive

import it.unibo.collektive.alignment.PrototypeMode
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * The component registrar registers the alignment generation extension if
 * the compiler plugin is enabled.
 */
@OptIn(ExperimentalCompilerApi::class)
class AlignmentComponentRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val enabled = configuration.get(AlignmentCommandLineProcessor.ARG_ENABLED) ?: error("No enabled arg")
        val alignmentMode = configuration.get(AlignmentCommandLineProcessor.PATH_ALIGNMENT_MODE)
            ?: PrototypeMode
        if (enabled) {
            IrGenerationExtension.registerExtension(AlignmentIrGenerationExtension(logger, alignmentMode))
        }
    }
}
