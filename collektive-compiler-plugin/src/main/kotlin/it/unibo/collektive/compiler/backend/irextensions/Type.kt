/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.backend.irextensions

import it.unibo.collektive.compiler.backend.util.withBetterSymbols
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classifierOrNull

/**
 * Extension function that checks if this [IrType] is assignable from another [IrType].
 *
 * Types are compared based on their classifier (symbolic name) equality.
 *
 * @receiver the base type
 * @param other the candidate type to assign
 * @return `true` if the types are considered assignable, `false` otherwise
 */
internal fun IrType.isAssignableFrom(other: IrType): Boolean = classifierOrNull?.let { base ->
    other.classifierOrNull?.let { other ->
        FqNameEqualityChecker.areEqual(base, other)
    } == true
} == true

/**
 * Returns a string representation of a list of [IrType]s,
 * optionally surrounded by [prefix] and [postfix].
 *
 * Null types are represented as `*`.
 *
 * @param prefix a string to prepend (default: `"("`)
 * @param postfix a string to append (default: `")"`)
 */
internal fun List<IrType?>.stringified(prefix: String = "(", postfix: String = ")"): String = when {
    isEmpty() -> ""
    else -> joinToString(",", prefix = prefix, postfix = postfix) {
        it?.classFqName?.asString()?.withBetterSymbols() ?: "*"
    }
}
