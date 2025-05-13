/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.aggregate.api.CollektiveIgnore
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.resolve.providers.firProvider

/**
 * Checks whether this [FirFunction] or any of its enclosing declarations
 * are annotated with [CollektiveIgnore].
 *
 * Functions or classes marked with [CollektiveIgnore] are excluded
 * from alignment and treated as purely local computations.
 *
 * @param context the [CheckerContext] used for the check
 * @return `true` if the function or a parent is annotated with [CollektiveIgnore]
 */
internal fun FirAnnotationContainer?.hasAnnotationDisablingPlugin(context: CheckerContext): Boolean = when (this) {
    null -> false
    else -> annotations.any { it.disablesPlugin() } ||
        context.hasAnnotationDisablingPlugin() ||
        when (this) {
            is FirFunction ->
                getContainingClass().hasAnnotationDisablingPlugin(context) ||
                    context.session.firProvider.getFirCallableContainerFile(this.symbol)
                        .hasAnnotationDisablingPlugin(context)
            else -> false
        }
}
