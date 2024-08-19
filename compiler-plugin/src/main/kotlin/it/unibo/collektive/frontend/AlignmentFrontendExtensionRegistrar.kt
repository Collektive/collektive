package it.unibo.collektive.frontend

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

/**
 * Extension registrar that adds frontend extensions to the compiler. It checks for dangerous or inappropriate code
 * written using the Collektive DSL
 */
class AlignmentFrontendExtensionRegistrar : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +::MissingAlignExtension
    }
}
