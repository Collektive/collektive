/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * Error messages for the Collektive compiler plugin.
 */
object FirCollektiveErrors {
    /**
     * Warning raised when a forbidden function is called.
     */
    val FORBIDDEN_FUNCTION_CALL by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when an aggregate function is called inside an iteration construct.
     */
    val AGGREGATE_FUNCTION_INSIDE_ITERATION by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when a function that has an aggregate parameter and uses it for making aggregate calls
     * is called inside an iteration construct.
     */
    val FUNCTION_WITH_AGGREGATE_PARAMETER_INSIDE_ITERATION by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when an aggregate call like `share`, `exchange`, `neighboring` or `evolve` is called without
     * using parameters inside the anonymous function, resulting in an unnecessary call.
     */
    val UNNECESSARY_CONSTRUCT_CALL by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when an aggregate call like `evolving`, `exchanging` or `sharing` is called and the expression
     * that is exchanged is the same as the one that is yielded inside the `yielding` block, therefore resulting in
     * an unnecessary yielding context that can be replaced with the same construct without it (`evolve`, `exchange` and
     * `share`).
     */
    val UNNECESSARY_YIELDING_CONTEXT by warning1<KtNameReferenceExpression, String>()

    init {
        RootDiagnosticRendererFactory.registerFactory(KtDefaultErrorMessagesCollektive)
    }
}
