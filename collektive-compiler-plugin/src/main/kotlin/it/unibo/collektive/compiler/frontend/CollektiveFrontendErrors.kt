/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend

import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * Collection of diagnostic messages used by the Collektive compiler plugin.
 */
object CollektiveFrontendErrors {

    /**
     * Warning raised when an aggregate function is called inside a loop or iteration construct
     * without an explicit alignment boundary.
     */
    val AGGREGATE_FUNCTION_INSIDE_ITERATION by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when a forbidden function (e.g., `align`, `dealign`) is explicitly called
     * within aggregate contexts.
     */
    val FORBIDDEN_FUNCTION_CALL by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when an `evolve`-like construct is used where a `share` would be more appropriate.
     *
     * Example: using `evolve` to perform a `neighboring` operation when the returned value
     * is equivalent across neighbors. This is semantically clearer and more performant with `share`.
     */
    val IMPROPER_EVOLVE_CONSTRUCT by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when a Collektive construct (like `share`, `exchange`, `neighboring`, `evolve`)
     * is invoked with an unused lambda parameter, making the call unnecessary.
     */
    val UNNECESSARY_CONSTRUCT_CALL by warning1<KtNameReferenceExpression, String>()

    /**
     * Warning raised when a yielding context is unnecessary — i.e., the yielded value
     * inside `yielding` is identical to the one passed to `sharing`, `evolving`, or `exchanging`.
     *
     * In such cases, the construct can be replaced by its non-yielding counterpart (`share`, `evolve`, `exchange`).
     */
    val UNNECESSARY_YIELDING_CONTEXT by warning1<KtNameReferenceExpression, String>()

    /**
     * Error raised when a branch (e.g., in a `when` or `if` expression) returns a value
     * of type [it.unibo.collektive.aggregate.Field].
     *
     * Returning fields from branches is disallowed due to potential misalignments across devices.
     */
    val BRANCH_RETURNS_FIELD by error1<KtExpression, String>()

    init {
        RootDiagnosticRendererFactory.registerFactory(CollektiveFrontendErrorMessageRenderer)
    }
}
