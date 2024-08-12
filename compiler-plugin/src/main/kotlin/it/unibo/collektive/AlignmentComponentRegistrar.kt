package it.unibo.collektive

import io.github.freshmag.collektiveplugin.AlignmentFrontendExtensionRegistrar
import it.unibo.collektive.utils.logging.info
import it.unibo.collektive.utils.logging.strongWarning
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

/**
 * The component registrar registers the alignment generation extension if
 * the compiler plugin is enabled.
 */
@OptIn(ExperimentalCompilerApi::class)
class AlignmentComponentRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        when (configuration.get(JVMConfigurationKeys.IR)) {
            false -> error(
                "The Kotlin-JVM IR backend has been explicitly disabled," +
                    " but the Collektive compiler plugin requires it",
            )

            null -> {
                when {
                    configuration.isReadOnly -> logger.strongWarning(
                        "The Kotlin-JVM IR backend has not been explicitly enabled and the compiler configuration has" +
                            "been finalized. The Collektive compiler plugin requires the IR generation," +
                            "the plugin may not be able to apply its transformations correctly",
                    )

                    else -> configuration.put(JVMConfigurationKeys.IR, true).also {
                        logger.info(
                            "Implicitly enabling the Kotlin-JVM IR backend," +
                                "it is required by the Collektive compiler plugin",
                        )
                    }
                }
            }

            else -> Unit
        }
        if (configuration.get(AlignmentCommandLineProcessor.ARG_ENABLED) != false) {
            IrGenerationExtension.registerExtension(AlignmentIrGenerationExtension(logger))
            FirExtensionRegistrarAdapter.registerExtension(AlignmentFrontendExtensionRegistrar())
        }
    }
}
