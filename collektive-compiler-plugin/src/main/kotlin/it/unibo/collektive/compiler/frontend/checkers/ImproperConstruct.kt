/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.checkers

import it.unibo.collektive.compiler.common.CollektiveNames.EVOLVE_FUNCTION_FQ_NAME
import it.unibo.collektive.compiler.common.CollektiveNames.EVOLVING_FUNCTION_FQ_NAME
import it.unibo.collektive.compiler.frontend.CollektiveFrontendErrors
import it.unibo.collektive.compiler.frontend.firextensions.fqName
import it.unibo.collektive.compiler.frontend.firextensions.functionName
import it.unibo.collektive.compiler.frontend.visitors.ImproperConstructVisitor
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

/**
 * Checker that detects improper usage of the `evolve` or `evolving` constructs
 * that could be more appropriately expressed using `share`.
 *
 * For example:
 *
 * ```kotlin
 * evolve(initial) { it ->
 *     val newValue = // same as `it`
 *     neighboring(newValue).operation // neighboring field is stored
 * }
 * ```
 *
 * This construct is flagged because it can typically be replaced with a `share` call,
 * which is more semantically accurate and efficient for this pattern.
 */
object ImproperConstruct : FirFunctionCallChecker(MppCheckerKind.Common) {

    private fun FirFunctionCall.isImproperEvolve(): Boolean {
        val visitor = ImproperConstructVisitor(fqName)
        visitor.visitElement(this)
        return visitor.isReplaceable
    }

    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val isEvolve = expression.fqName.run { this == EVOLVE_FUNCTION_FQ_NAME || this == EVOLVING_FUNCTION_FQ_NAME }
        if (isEvolve && expression.isImproperEvolve()) {
            reporter.reportOn(
                expression.calleeReference.source,
                CollektiveFrontendErrors.IMPROPER_EVOLVE_CONSTRUCT,
                expression.functionName,
                context,
            )
        }
    }
}
