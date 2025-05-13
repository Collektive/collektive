/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol

/**
 * Checks whether this function symbol or its surrounding context is annotated to disable the Collektive plugin.
 *
 * This function returns `true` if either:
 * - The function itself is annotated with a plugin-disabling annotation, or
 * - Any of the surrounding elements in the given [context] are annotated to disable the plugin.
 *
 * @receiver the [FirFunctionSymbol] to inspect
 * @param context the [CheckerContext] providing access to surrounding FIR elements
 * @return `true` if plugin checks should be disabled, `false` otherwise
 */
fun FirFunctionSymbol<*>.hasAnnotationDisablingPlugin(context: CheckerContext): Boolean =
    annotations.any { it.disablesPlugin() } || context.hasAnnotationDisablingPlugin()

/**
 * Determines whether this function symbol represents an aggregate function.
 *
 * A function is considered aggregate if:
 * - It is not annotated with any annotation that disables the Collektive plugin.
 * - At least one of its receivers or arguments is of an aggregate-related type.
 *
 * @receiver the [FirFunctionSymbol] to analyze (nullable)
 * @param context the [CheckerContext], used to access the current compilation context
 * @return `true` if the function should be treated as aggregate, `false` otherwise
 */
@OptIn(SymbolInternals::class)
internal fun FirFunctionSymbol<*>?.isAggregate(context: CheckerContext): Boolean = this?.fir.isAggregate(context)
