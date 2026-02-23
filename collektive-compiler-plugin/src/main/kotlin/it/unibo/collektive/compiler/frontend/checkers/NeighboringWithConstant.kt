/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.checkers

import it.unibo.collektive.compiler.common.CollektiveNames.NEIGHBORING_FUNCTION_FQ_NAME
import it.unibo.collektive.compiler.frontend.CollektiveFrontendErrors
import it.unibo.collektive.compiler.frontend.firextensions.fqName
import it.unibo.collektive.compiler.frontend.firextensions.functionName
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirLiteralExpression

/**
 * Checker that detects calls to `neighboring` with a compile-time constant argument.
 *
 * When a constant is passed to `neighboring`, the constant value is shared with all neighbors,
 * even though it is locally known and never changes. This is inefficient, as it causes
 * unnecessary network communication.
 *
 * The recommended alternative is `mapNeighborhood { constant }`, which uses minimal communication:
 * only the alignment token is shared, and the constant is reconstructed locally.
 *
 * For example:
 * ```kotlin
 * // Inefficient: shares `42` with neighbors
 * neighboring(42)
 *
 * // Efficient: only shares alignment token; reconstructs 42 locally
 * mapNeighborhood { 42 }
 * ```
 */
object NeighboringWithConstant : FirFunctionCallChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        if (expression.fqName != NEIGHBORING_FUNCTION_FQ_NAME) return
        val argument = expression.argumentList.arguments.singleOrNull() ?: return
        if (argument is FirLiteralExpression) {
            reporter.reportOn(
                expression.calleeReference.source,
                CollektiveFrontendErrors.NEIGHBORING_WITH_CONSTANT,
                expression.functionName,
            )
        }
    }
}
