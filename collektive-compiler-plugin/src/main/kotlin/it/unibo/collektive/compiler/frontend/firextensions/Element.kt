/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.frontend.firextensions

import it.unibo.collektive.compiler.common.CollektiveNames
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.utils.classId
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol

/**
 * Returns `true` if this [FirElement] is a call to the `alignedOn` function.
 */
internal fun FirElement.isAlignedOn(): Boolean =
    this is FirFunctionCall && functionName == CollektiveNames.ALIGNED_ON_FUNCTION_NAME

/**
 * Returns a readable, human-friendly string representation of the current [FirElement].
 *
 * This is used primarily for debugging or logging purposes to better understand
 * the type or identity of a given FIR node.
 */
internal fun FirElement.niceString(): String = when (this) {
    is FirFunctionCall -> functionName
    is FirSimpleFunction -> name.asString()
    is FirClassLikeDeclaration -> classId.asString()
    is FirClassSymbol<*> -> classId.asString()
    is FirFile -> name
    else ->
        this::class.simpleName
            ?.removePrefix("Fir")
            ?.removeSuffix("Impl")
            ?.removeSuffix("Expression")
            ?: "Unknown"
}
