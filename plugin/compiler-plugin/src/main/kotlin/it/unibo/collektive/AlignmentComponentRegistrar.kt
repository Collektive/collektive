package it.unibo.collektive

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
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
        val enabled = configuration.get(AlignmentCommandLineProcessor.ARG_ENABLED) ?: error("No enabled arg")
        if (enabled) {
            IrGenerationExtension.registerExtension(AlignmentIrGenerationExtension())
        }
    }
}
