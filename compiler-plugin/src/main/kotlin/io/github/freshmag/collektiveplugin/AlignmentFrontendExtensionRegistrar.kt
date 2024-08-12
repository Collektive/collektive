package io.github.freshmag.collektiveplugin

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class AlignmentFrontendExtensionRegistrar : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +::AlignRawCallExtension
    }
}