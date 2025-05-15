/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.references.toResolvedFunctionSymbol
import org.jetbrains.kotlin.fir.references.toResolvedNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

/**
 * Retrieves the simple name of the called function in this [FirFunctionCall].
 */
internal val FirFunctionCall.functionName: String
    get() = calleeReference.name.asString()

/**
 * Retrieves the fully qualified name of the function being called by this [FirFunctionCall].
 *
 * If the function is a top-level declaration, it returns `packageName.functionName`.
 * If the function is inside a class, it returns `packageName.className.functionName`.
 */
internal val FirFunctionCall.fqName: String
    get() {
        val callableId = calleeReference.toResolvedFunctionSymbol()?.callableId
        val packageName = callableId?.packageName?.asString()
        val className = callableId?.className?.asString()
        val functionName = callableId?.callableName?.asString()
        return if (className != null) {
            "$packageName.$className.$functionName"
        } else {
            "$packageName.$functionName"
        }
    }

/**
 * Determines whether this [FirFunctionCall] represents a call to an aggregate-aware function.
 *
 * A function is considered aggregate-aware if:
 * - It is not disabled via annotation.
 * - It has a receiver or parameter that matches aggregate-related types.
 *
 * @param context the [CheckerContext] for resolution
 * @return `true` if the function call involves aggregate types; `false` otherwise
 */
internal fun FirFunctionCall?.isAggregate(context: CheckerContext): Boolean = when (this) {
    null -> false
    else -> !hasAnnotationDisablingPlugin(context) &&
        this.calleeReference.toResolvedNamedFunctionSymbol().isAggregate(context)
}

/**
 * Returns the sequence of receiver and argument types associated with this [FirFunctionCall].
 */
internal fun FirFunctionCall.receiversAndArgumentsTypes(): Sequence<ConeKotlinType> =
    calleeReference.toResolvedNamedFunctionSymbol()?.receiversAndArgumentsTypes().orEmpty()
