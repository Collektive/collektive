/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

/**
 * Computes the full sequence of receiver and argument types for a given callable symbol.
 *
 * Includes, in order:
 * - Context parameters
 * - Value parameters
 * - Dispatch receiver
 * - Extension receiver
 *
 * Useful for checking type assignability or inspecting function signatures in FIR analysis.
 *
 * @receiver the [FirCallableSymbol] whose signature is to be inspected
 * @return a [Sequence] of [ConeKotlinType]s representing all input types
 */
internal fun FirCallableSymbol<*>.receiversAndArgumentsTypes(): Sequence<ConeKotlinType> {
    val valueParameters: Sequence<ConeKotlinType> = (this as? FirFunctionSymbol<*>)
        ?.valueParameterSymbols?.asSequence()?.map { it.resolvedReturnType }.orEmpty()
    val receiver = sequenceOf(resolvedReceiverTypeRef?.coneType).filterNotNull()
    val dispatchReceiver = sequenceOf(dispatchReceiverType).filterNotNull()
    val contextParameters: Sequence<ConeKotlinType> = this.contextParameterSymbols.asSequence().map { it.resolvedReturnType }
    return contextParameters + valueParameters + dispatchReceiver + receiver
}
