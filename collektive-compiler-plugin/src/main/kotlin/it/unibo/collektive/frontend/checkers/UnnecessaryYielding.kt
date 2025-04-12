/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.frontend.visitors.YieldingUnnecessaryUsageVisitor
import it.unibo.collektive.utils.common.AggregateFunctionNames.EVOLVING_FUNCTION_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.EXCHANGING_FUNCTION_FQ_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.SHARING_FUNCTION_FQ_NAME
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

/**
 * Checker for unnecessary yielding contexts.
 *
 * This checker is responsible for finding constructs like `evolving`, `exchanging` and `sharing` that are called with
 * a yielded expression that is the same as the one that is exchanged, resulting in an unnecessary yielding context.
 *
 * For example:
 *
 * ```kotlin
 * sharing(initial) {
 *     // ...
 *     value.yielding { value }
 * }
 * ```
 *
 * Should generate a warning indicating to switch to the `share` construct, as in the following:
 *
 * ```kotlin
 * share(initial) {
 *    // ...
 *    value
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

    private fun FirFunctionCall.usesAnUnnecessaryYieldingContext(): Boolean = with(YieldingUnnecessaryUsageVisitor()) {
        containsUnnecessaryYielding()
    }

    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression.fqName() in constructs && expression.usesAnUnnecessaryYieldingContext()) {
            reporter.reportOn(
                expression.calleeReference.source,
                FirCollektiveErrors.UNNECESSARY_YIELDING_CONTEXT,
                expression.functionName(),
                context,
            )
        }
    }
}
