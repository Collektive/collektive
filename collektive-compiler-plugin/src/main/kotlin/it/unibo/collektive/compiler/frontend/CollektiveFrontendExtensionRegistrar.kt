/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

/**
 * Extension registrar that adds frontend extensions to the compiler. It checks for dangerous or inappropriate code
 * written using the Collektive DSL
 */
class CollektiveFrontendExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::CollektiveExtension
    }
}
