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
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol

/**
 * Determines whether a given [FirFunction] involves aggregate-typed receivers or parameters.
 */
internal fun FirDeclaration?.isAggregate(context: CheckerContext): Boolean = when (this) {
    null -> false
    else -> !hasAnnotationDisablingPlugin(context) &&
        receiversAndArgumentsTypes().anyIsAggregate(context.session)
}

/**
 * Returns the types of receivers and arguments associated with this [FirFunction].
 */
internal fun FirDeclaration.receiversAndArgumentsTypes() =
    (symbol as? FirCallableSymbol<*>)?.receiversAndArgumentsTypes().orEmpty()
