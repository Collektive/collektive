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
        configuration.requireConfiguration(CommonConfigurationKeys.USE_FIR, true, logger) {
            "The IR verification has been explicitly disabled, but the Collektive compiler plugin requires it"
        }
        if (configuration.get(AlignmentCommandLineProcessor.ARG_ENABLED) != false) {
            IrGenerationExtension.registerExtension(AlignmentIrGenerationExtension(logger))
            FirExtensionRegistrarAdapter.registerExtension(CollektiveFrontendExtensionRegistrar())
        }
    }

    private fun <T> CompilerConfiguration.requireConfiguration(
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
