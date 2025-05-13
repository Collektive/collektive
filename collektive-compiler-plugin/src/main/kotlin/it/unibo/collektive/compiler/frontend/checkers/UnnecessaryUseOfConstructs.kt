/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.checkers

import it.unibo.collektive.compiler.common.CollektiveNames
import it.unibo.collektive.compiler.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.compiler.frontend.checkers.CheckersUtility.functionName
import it.unibo.collektive.compiler.frontend.visitors.ConstructCallVisitor
import it.unibo.collektive.compiler.frontend.visitors.EmptyReturnVisitor
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
            CollektiveNames.NEIGHBORING_FUNCTION_FQ_NAME,
            CollektiveNames.EXCHANGE_FUNCTION_FQ_NAME,
            CollektiveNames.SHARE_FUNCTION_FQ_NAME,
            CollektiveNames.EVOLVE_FUNCTION_FQ_NAME,
            CollektiveNames.EXCHANGING_FUNCTION_FQ_NAME,
            CollektiveNames.SHARING_FUNCTION_FQ_NAME,
            CollektiveNames.EVOLVING_FUNCTION_FQ_NAME,
        )

    private fun FirFunctionCall.isConstructToCheck() = fqName() in constructs

    private fun FirFunctionCall.doesNotUseParameter(): Boolean = when (fqName()) {
        CollektiveNames.NEIGHBORING_FUNCTION_FQ_NAME -> with(EmptyReturnVisitor()) { hasEmptyReturn() }
        else -> with(ConstructCallVisitor()) { doesNotContainValueParameterUsagesInAnonymousFunctionCall() }
    }

    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
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
