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
 * A FIR (Front-end IR) expression checker that verifies whether `when` expressions
 * inside aggregate functions return values of type [it.unibo.collektive.aggregate.Field].
 *
 * ### Purpose
 * This checker ensures **semantic alignment safety** in aggregate programs by disallowing
 * the return of `Field` objects from conditional branches such as `when` expressions.
 * Returning `Field` instances from different branches can cause **alignment issues**
 * that violate the aggregate computing model enforced by Collektive.
 *
 * ### Applicability
 * This checker only triggers when:
 * - The `when` expression appears inside an aggregate function
 * (as identified by [CheckersUtility.isInsideAggregateFunction]).
 * - The current function is *not* `it.unibo.collektive.aggregate.api.impl.project`.
 * - The `when` expression's resolved type corresponds to [it.unibo.collektive.aggregate.Field].
 *
 * ### Example Violation
 * ```kotlin
 * val result = when {
 *     cond1 -> neighboring(1)
 *     else -> neighboring(2)
 * }
 * ```
 * This will raise an error because both branches return `Field` objects.
 *
 * ### Suggested Fix
 * Instead of returning `Field`s directly from branches, refactor to use `alignedMapValues`:
 * ```kotlin
 * neighboring(1).alignedMapValues(neighboring(2)) { _, y, z -> if (cond1) y else z }
 * ```
 *
 * @see org.jetbrains.kotlin.fir.analysis.checkers.expression.FirWhenExpressionChecker
 * @see it.unibo.collektive.utils.common.AggregateFunctionNames.FIELD_CLASS_FQ_NAME
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
