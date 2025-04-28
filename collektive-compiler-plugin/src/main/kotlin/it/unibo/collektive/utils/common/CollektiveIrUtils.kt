/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.utils.common

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.receiverAndArgs

internal fun IrType.isAssignableFrom(other: IrType): Boolean = classifierOrNull?.let { base ->
    other.classifierOrNull?.let { other ->
        FqNameEqualityChecker.areEqual(base, other)
    } == true
} == true

private fun List<IrType?>.stringified(prefix: String = "(", postfix: String = ")"): String = when {
    isEmpty() -> ""
    else -> joinToString(",", prefix = prefix, postfix = postfix) {
        it?.classFqName?.asString()?.withBetterSymbols() ?: "*"
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrCall.getAlignmentToken(): String {
    val symbolOwner = symbol.owner
    val arguments = receiverAndArgs().map { it.type }.stringified()
    val generics = typeArguments.stringified("<", ">")
    return when {
        symbolOwner.name.isSpecial -> "λ"
        else -> symbolOwner.kotlinFqName.asString().withBetterSymbols() + generics + arguments
    }
}

private val replacements = listOf(
    "it.unibo.alchemist." to "⚗️",
    "it.unibo.collektive.aggregate.api.Aggregate._ serialization aware neighboring" to "↔",
    "it.unibo.collektive.aggregate.api.Aggregate._ serialization aware exchanging" to "🔄",
    "it.unibo.collektive.aggregate.api.Aggregate.InternalAPI" to "🔐",
    "it.unibo.collektive.aggregate.api.DataSharingMethod" to "💾",
    "it.unibo.collektive.field.Field" to "φ",
    "kotlin.Function" to "ƒ_",
)

private val removedPrefixes = listOf(
    "kotlin.",
    "it.unibo.collektive.",
)

private fun String.withBetterSymbols(): String {
    val clean = replacements.fold(this) { current, (replaced, replacement) -> current.replace(replaced, replacement) }
    return when {
        removedPrefixes.any { clean.startsWith(it) } -> "\u200B${clean.substringAfterLast('.')}"
        else -> clean
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrCall.simpleFunctionName(): String = symbol.owner.name.asString()

internal fun <T : IrElement> irStatement(
    pluginContext: IrPluginContext,
    functionToAlign: IrFunction,
    expression: IrElement,
    body: IrBlockBodyBuilder.() -> T,
): T = IrBlockBodyBuilder(
    pluginContext,
    Scope(functionToAlign.symbol),
    expression.startOffset,
    expression.endOffset,
).body()
