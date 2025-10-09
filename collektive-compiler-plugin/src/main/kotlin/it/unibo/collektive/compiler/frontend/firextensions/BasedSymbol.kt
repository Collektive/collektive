/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals

/**
 * Determines whether this symbol represents an aggregate function.
 *
 * @receiver the [FirBasedSymbol] to analyze (nullable)
 * @param context the [CheckerContext], used to access the current compilation context
 * @return `true` if the function should be treated as aggregate, `false` otherwise
 */
@OptIn(SymbolInternals::class)
internal fun FirBasedSymbol<*>?.isAggregate(context: CheckerContext): Boolean = this?.fir.isAggregate(context)
