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

internal fun FirElement.isAlignedOn() =
    this is FirFunctionCall && functionName == CollektiveNames.ALIGNED_ON_FUNCTION_NAME

/**
 * TODO: for debugging purposes.
 */
internal fun FirElement.niceString() = when (this) {
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
