/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.frontend.checkers

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

/**
 * Checker that looks for improper usages of the `evolve` method, generating a warning.
 * For example, in the following example:
 *
 * ```kotlin
 * evolve(initial){ it ->
 *     val newValue = //same of `it`
 *     neighboring(newValue).operation //field reduction
 * }
 * ```
 * The checker raises a warning because this operation can be replaced by using the more appropriate `share` construct.
 */
object ImproperConstruct : FirFunctionCallChecker(MppCheckerKind.Common) {
    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        // TODO: Implement the checker
    }
}
