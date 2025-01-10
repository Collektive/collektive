/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import it.unibo.collektive.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.frontend.visitors.ConstructCallVisitor
import it.unibo.collektive.frontend.visitors.EmptyReturnVisitor
import it.unibo.collektive.utils.common.AggregateFunctionNames
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

/**
 * Checker for unnecessary use of constructs.
 *
 * This checker is responsible for detecting unnecessary calls to constructs like `share`, `exchange`, `neighboring`
 * or `evolve`.
 *
 * For example:
 *
 * ```kotlin
 * evolve(initial) { it ->
 *    // but `it` is not used inside the body
 * }
 * ```
 *
 * Should generate a warning indicating the unnecessary use of the `evolve` construct.
 */
object UnnecessaryUseOfConstructs : FirFunctionCallChecker(MppCheckerKind.Common) {
    private val constructs =
        listOf(
            AggregateFunctionNames.NEIGHBORING_FUNCTION_FQ_NAME,
            AggregateFunctionNames.EXCHANGE_FUNCTION_FQ_NAME,
            AggregateFunctionNames.SHARE_FUNCTION_FQ_NAME,
            AggregateFunctionNames.EVOLVE_FUNCTION_FQ_NAME,
            AggregateFunctionNames.NEIGHBORING_VIA_EXCHANGE_FUNCTION_FQ_NAME,
            AggregateFunctionNames.EXCHANGING_FUNCTION_FQ_NAME,
            AggregateFunctionNames.SHARING_FUNCTION_FQ_NAME,
            AggregateFunctionNames.EVOLVING_FUNCTION_FQ_NAME,
        )

    private fun FirFunctionCall.isConstructToCheck() = fqName() in constructs

    private fun FirFunctionCall.doesNotUseParameter(): Boolean =
        if (fqName().run {
                this == AggregateFunctionNames.NEIGHBORING_FUNCTION_FQ_NAME ||
                    this == AggregateFunctionNames.NEIGHBORING_VIA_EXCHANGE_FUNCTION_FQ_NAME
            }
        ) {
            with(EmptyReturnVisitor()) {
                hasEmptyReturn()
            }
        } else {
            with(ConstructCallVisitor()) {
                doesNotContainValueParameterUsagesInAnonymousFunctionCall()
            }
        }

    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (expression.isConstructToCheck() && expression.doesNotUseParameter()) {
            reporter.reportOn(
                expression.calleeReference.source,
                FirCollektiveErrors.UNNECESSARY_CONSTRUCT_CALL,
                expression.functionName(),
                context,
            )
        }
    }
}
