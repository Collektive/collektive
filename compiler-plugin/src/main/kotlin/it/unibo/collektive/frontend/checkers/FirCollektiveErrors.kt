/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.CALL_ELEMENT_WITH_DOT
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning1

/**
 * Error messages for the Collektive compiler plugin.
 */
object FirCollektiveErrors {
    /**
     * Warning raised when a forbidden function is called.
     */
    val FORBIDDEN_FUNCTION_CALL by warning1<PsiElement, String>(CALL_ELEMENT_WITH_DOT)

    /**
     * Warning raised when an aggregate function is called inside an iteration construct.
     */
    val AGGREGATE_FUNCTION_INSIDE_ITERATION by warning1<PsiElement, String>(CALL_ELEMENT_WITH_DOT)

    /**
     * Warning raised when a function that has an aggregate parameter and uses it for making aggregate calls
     * is called inside an iteration construct.
     */
    val FUNCTION_WITH_AGGREGATE_PARAMETER_INSIDE_ITERATION by warning1<PsiElement, String>(CALL_ELEMENT_WITH_DOT)

    init {
        RootDiagnosticRendererFactory.registerFactory(KtDefaultErrorMessagesCollektive)
    }
}
