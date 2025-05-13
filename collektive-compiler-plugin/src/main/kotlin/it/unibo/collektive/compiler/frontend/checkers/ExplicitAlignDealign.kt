/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.checkers

import it.unibo.collektive.compiler.common.CollektiveNames
import it.unibo.collektive.compiler.frontend.checkers.CheckersUtility.fqName
import it.unibo.collektive.compiler.frontend.checkers.CheckersUtility.isInsideAggregateFunction
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

/**
 * Checker that looks for usages of "align" and "dealign" methods inside Aggregate functions, generating a warning.
 */
object ExplicitAlignDealign : FirFunctionCallChecker(MppCheckerKind.Common) {
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val fqnCalleeName = expression.fqName()
        if (fqnCalleeName in FORBIDDEN_FUNCTIONS && context.isInsideAggregateFunction()) {
            reporter.reportOn(
                expression.calleeReference.source,
                FirCollektiveErrors.FORBIDDEN_FUNCTION_CALL,
                fqnCalleeName,
                context,
            )
        }
    }

    private val FORBIDDEN_FUNCTIONS =
        listOf(
            CollektiveNames.ALIGN_FUNCTION_FQ_NAME,
            CollektiveNames.DEALIGN_FUNCTION_FQ_NAME,
        )
}
