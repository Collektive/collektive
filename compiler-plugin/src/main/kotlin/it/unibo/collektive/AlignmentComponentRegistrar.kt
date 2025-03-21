package it.unibo.collektive

import it.unibo.collektive.frontend.CollektiveFrontendExtensionRegistrar
import it.unibo.collektive.utils.logging.info
import it.unibo.collektive.utils.logging.strongWarning
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
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
        val logger = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        when (configuration.get(CommonConfigurationKeys.USE_FIR)) {
            false ->
                error(
                    "The IR verification has been explicitly disabled," +
                        " but the Collektive compiler plugin requires it",
                )
            null -> {
                when {
                    configuration.isReadOnly ->
                        logger.strongWarning(
                            "The IR verification has not been explicitly enabled and the compiler configuration" +
                                "has been finalized. The Collektive compiler plugin requires the IR verification," +
                                "the plugin may not be able to apply its transformations correctly",
                        )

                    else ->
                        configuration.put(CommonConfigurationKeys.USE_FIR, true).also {
                            logger.info(
                                "Implicitly enabling the IR verification," +
                                    "it is required by the Collektive compiler plugin",
                            )
                        }
                }
            }
            else -> Unit

        }
        if (configuration.get(AlignmentCommandLineProcessor.ARG_ENABLED) != false) {
            IrGenerationExtension.registerExtension(AlignmentIrGenerationExtension(logger))
            FirExtensionRegistrarAdapter.registerExtension(CollektiveFrontendExtensionRegistrar())
        }
    }
}
