/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.compiler.frontend.visitors.ReturnsUnitVisitor
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.impl.FirUnitExpression
import org.jetbrains.kotlin.fir.references.toResolvedFunctionSymbol
import org.jetbrains.kotlin.fir.references.toResolvedNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

/**
 * Returns the name of the called function in a [FirFunctionCall] element.
 */
internal val FirFunctionCall.functionName get(): String = calleeReference.name.asString()

/**
 * Returns the fully qualified name of this [FirFunctionCall].
 */
internal val FirFunctionCall.fqName get(): String {
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
 * Determines whether a given [FirFunctionCall] involves aggregate-typed receivers or arguments.
 */
internal fun FirFunctionCall?.isAggregate(context: CheckerContext): Boolean = when (this) {
    null -> false
    else -> !hasAnnotationDisablingPlugin(context) &&
        this.calleeReference.toResolvedNamedFunctionSymbol().isAggregate(context)
}

/**
 * Returns the types of receivers and arguments associated with this [FirFunctionCall].
 */
internal fun FirFunctionCall.receiversAndArgumentsTypes(): Sequence<ConeKotlinType> =
    calleeReference.toResolvedNamedFunctionSymbol()?.receiversAndArgumentsTypes().orEmpty()

/**
 * Check if the function call has an empty return (i.e., no return statement, resulting into a [FirUnitExpression]).
 * Returns `true` if the function call has an empty return, `false` otherwise.
 */
internal fun FirFunctionCall.returnsUnit(): Boolean = ReturnsUnitVisitor.returnsUnit(this)
