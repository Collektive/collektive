/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend

import it.unibo.collektive.compiler.frontend.checkers.ConstructIgnoresParameter
import it.unibo.collektive.compiler.frontend.checkers.ExplicitAlignDealign
import it.unibo.collektive.compiler.frontend.checkers.ImproperConstruct
import it.unibo.collektive.compiler.frontend.checkers.NoAlignInsideLoop
import it.unibo.collektive.compiler.frontend.checkers.UnnecessaryYielding
import it.unibo.collektive.compiler.frontend.checkers.WhenReturnsField
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirWhenExpressionChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

/**
 * Extension that adds a series of checkers that looks for improper usages of the Collektive DSL.
 */
class CollektiveExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val expressionCheckers: ExpressionCheckers =
        object : ExpressionCheckers() {
            override val functionCallCheckers: Set<FirFunctionCallChecker>
                get() =
                    setOf(
                        ExplicitAlignDealign,
                        ImproperConstruct,
                        NoAlignInsideLoop,
                        ConstructIgnoresParameter,
                        UnnecessaryYielding,
                    )

            override val whenExpressionCheckers: Set<FirWhenExpressionChecker>
                get() = setOf(
                    WhenReturnsField,
                )
        }
}
