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
 * Returns `true` if this [IrType] is assignable from the [other] type.
 *
 * Types are compared by checking whether their classifiers (symbolic type references)
 * are equal using [FqNameEqualityChecker].
 *
 * @receiver the base type
 * @param other the candidate type to assign
 * @return `true` if [other] can be assigned to this type; `false` otherwise
 */
internal fun IrType.isAssignableFrom(other: IrType): Boolean = classifierOrNull?.let { base ->
    other.classifierOrNull?.let { other ->
        FqNameEqualityChecker.areEqual(base, other)
    } == true
} == true

/**
 * Returns a stringified representation of a list of [IrType]s,
 * optionally surrounded by [prefix] and [postfix].
 *
 * Null types are rendered as `"*"`.
 *
 * @param prefix a string to prepend (default: `"("`)
 * @param postfix a string to append (default: `")"`)
 * @return a formatted string representing the list of types
 */
internal fun List<IrType?>.stringified(prefix: String = "(", postfix: String = ")"): String = when {
    isEmpty() -> ""
    else -> joinToString(",", prefix = prefix, postfix = postfix) {
        it?.classFqName?.asString()?.withBetterSymbols() ?: "*"
    }
}
