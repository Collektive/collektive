/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol

/**
 * Determines whether this [FirDeclaration] represents an aggregate-aware function.
 *
 * A function is considered aggregate-aware if:
 * - It is not annotated with a plugin-disabling annotation.
 * - At least one of its receiver or argument types is aggregate-related.
 *
 * @receiver the [FirDeclaration] to inspect
 * @param context the [CheckerContext] providing type and annotation resolution
 * @return `true` if the declaration is aggregate-aware; `false` otherwise
 */
internal fun FirDeclaration?.isAggregate(context: CheckerContext): Boolean = when (this) {
    null -> false
    else -> !hasAnnotationDisablingPlugin(context) &&
        receiversAndArgumentsTypes().anyIsAggregate(context.session)
}

/**
 * Retrieves all receiver and argument types associated with this [FirDeclaration].
 *
 * @receiver the declaration to inspect
 * @return a sequence of input types, or empty if the declaration is not callable
 */
internal fun FirDeclaration.receiversAndArgumentsTypes() =
    (symbol as? FirCallableSymbol<*>)?.receiversAndArgumentsTypes().orEmpty()
