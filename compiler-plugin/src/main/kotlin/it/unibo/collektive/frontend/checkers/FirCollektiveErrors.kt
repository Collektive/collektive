/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning1

object FirCollektiveErrors {
    val FORBIDDEN_FUNCTION by warning1<PsiElement, String>(SourceElementPositioningStrategies.CALL_ELEMENT_WITH_DOT)
    val AGGREGATE_FUNCTION_INSIDE_ITERATION by warning1<PsiElement, String>(
        SourceElementPositioningStrategies.CALL_ELEMENT_WITH_DOT,
    )

    init {
        RootDiagnosticRendererFactory.registerFactory(KtDefaultErrorMessagesCollektive)
    }
}