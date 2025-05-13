/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.checkers

import it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME
import it.unibo.collektive.compiler.frontend.CollektiveFrontendErrors
import it.unibo.collektive.compiler.frontend.firextensions.isAggregateProjection
import it.unibo.collektive.compiler.frontend.firextensions.isInsideAggregateFunction
import it.unibo.collektive.compiler.frontend.firextensions.returnsAField
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirWhenExpressionChecker
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.type

/**
 * Checker that verifies whether `when` expressions inside aggregate functions
 * return values of type [it.unibo.collektive.aggregate.Field].
 *
 * ### Purpose
 * Ensures **semantic alignment safety** by preventing `Field` values
 * from being returned directly from conditional branches. This helps avoid
 * misaligned computations in aggregate DSL contexts.
 *
 * ### Conditions for triggering
 * - The `when` expression is located inside an aggregate function
 *   (detected via [CheckerContext.isInsideAggregateFunction]).
 * - The current function is *not* the `project` helper.
 * - The `when` expression resolves to a `Field` type.
 *
 * ### Example (violates rule)
 * ```kotlin
 * val result = when {
 *     cond1 -> neighboring(1)
 *     else -> neighboring(2)
 * }
 * ```
 * This triggers a diagnostic because `Field` instances are returned directly.
 *
 * ### Suggested Fix
 * Instead of returning `Field`s from conditional branches, use `alignedMapValues`:
 * ```kotlin
 * neighboring(1).alignedMapValues(neighboring(2)) { _, y, z -> if (cond1) y else z }
 * ```
 *
 * @see org.jetbrains.kotlin.fir.analysis.checkers.expression.FirWhenExpressionChecker
 * @see it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME
 */
object WhenReturnsField : FirWhenExpressionChecker(MppCheckerKind.Common) {
    override fun check(expression: FirWhenExpression, context: CheckerContext, reporter: DiagnosticReporter) {
        val isInsideAggregate by lazy { context.isInsideAggregateFunction() }
        val isNotProject by lazy { !context.isAggregateProjection() }
        expression.branches.asSequence().map { it.result }.forEach { branch: FirBlock ->
            if (branch.returnsAField() && isInsideAggregate && isNotProject) {
                reporter.reportOn(
                    branch.source,
                    CollektiveFrontendErrors.BRANCH_RETURNS_FIELD,
                    FIELD_CLASS_FQ_NAME + expression.resolvedType.typeArguments.joinToString(
                        prefix = "<",
                        postfix = ">",
                        separator = ", ",
                    ) { (it.type?.classId?.shortClassName ?: it.type).toString() },
                    context,
                )
            }
        }
    }
}
