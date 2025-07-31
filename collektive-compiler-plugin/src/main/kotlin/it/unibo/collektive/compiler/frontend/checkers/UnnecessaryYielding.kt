/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.checkers

import it.unibo.collektive.compiler.common.CollektiveNames.EVOLVING_FUNCTION_FQ_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.EXCHANGING_FUNCTION_FQ_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.SHARING_FUNCTION_FQ_NAME
import it.unibo.collektive.compiler.frontend.CollektiveFrontendErrors
import it.unibo.collektive.compiler.frontend.firextensions.fqName
import it.unibo.collektive.compiler.frontend.firextensions.functionName
import it.unibo.collektive.compiler.frontend.visitors.UnnecessaryYieldingVisitor
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

/**
 * Checker that identifies unnecessary yielding contexts.
 *
 * This checker detects cases where constructs like `evolving`, `exchanging`, or `sharing`
 * are called with a yielded expression that simply returns the same value,
 * making the use of `yielding` redundant.
 *
 * ### Example (flagged):
 * ```kotlin
 * sharing(initial) {
 *     // ...
 *     value.yielding { value }
 * }
 * ```
 *
 * In the above, the yielding context is unnecessary and should be replaced with:
 *
 * ### Preferred:
 * ```kotlin
 * share(initial) {
 *     // ...
 *     value
 * }
 * ```
 */
object UnnecessaryYielding : FirFunctionCallChecker(MppCheckerKind.Common) {

    private val constructs =
        listOf(
            EVOLVING_FUNCTION_FQ_NAME,
            EXCHANGING_FUNCTION_FQ_NAME,
            SHARING_FUNCTION_FQ_NAME,
        )

    private fun FirFunctionCall.usesAnUnnecessaryYieldingContext(): Boolean =
        UnnecessaryYieldingVisitor().containsUnnecessaryYielding(this)

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        if (expression.fqName in constructs && expression.usesAnUnnecessaryYieldingContext()) {
            reporter.reportOn(
                expression.calleeReference.source,
                CollektiveFrontendErrors.UNNECESSARY_YIELDING_CONTEXT,
                expression.functionName,
            )
        }
    }
}
