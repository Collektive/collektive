/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.utils.common

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.receiverAndArgs

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
private fun List<IrType?>.stringified(prefix: String = "(", postfix: String = ")"): String = when {
    isEmpty() -> ""
    else -> joinToString(",", prefix = prefix, postfix = postfix) {
        it?.classFqName?.asString()?.withBetterSymbols() ?: "*"
    }
}

/**
 * Returns `true` if this [IrFunctionAccessExpression] is a getter call.
 *
 * Recognizes synthetic Kotlin getter functions by their mangled name.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
val IrFunctionAccessExpression.isGetter
    get() = this is IrCall && symbol.owner.kotlinFqName.asString().substringAfterLast('.').startsWith("<get-")

/**
 * Returns the fully qualified name of this [IrDeclarationParent] as a string.
 */
private val IrDeclarationParent.fqString get() = this.kotlinFqName.asString()

/**
 * Computes a unique token that identifies this [IrFunctionAccessExpression]
 * for alignment purposes.
 *
 * The token encodes:
 * - the called function or constructor
 * - the type arguments
 * - the types of receivers and arguments
 *
 * @receiver the function or constructor call
 * @return a string token representing the call semantics
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrFunctionAccessExpression.toFunctionAlignmentToken(): String {
    val symbolOwner = symbol.owner
    val ownerName = symbolOwner.fqString
    val arguments = receiverAndArgs().map { it.type }.stringified()
    val generics = typeArguments.stringified("<", ">")
    return when {
        this is IrConstructorCall ->
            (type.classFqName?.asString() ?: "anonymous-init").withBetterSymbols() + generics + arguments
        isGetter -> {
            val receiver = receiverAndArgs().single().type.classFqName
                ?.asString()
                ?.withBetterSymbols()
                ?: "anonymous-type"
            val property = ownerName.substringAfterLast('<')
            "$receiver.${property.substring(4..(property.length - 2))}"
        }
        symbolOwner.name.isSpecial -> "λ$generics$arguments"
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

/**
 * Improves the readability of a fully qualified name by replacing
 * well-known package prefixes and symbolic names with emojis or shorter symbols.
 *
 * Used mainly to generate compact, user-friendly alignment tokens.
 *
 * @receiver the original fully qualified name
 * @return a compact and symbolic representation
 */
private fun String.withBetterSymbols(): String {
    val clean = replacements.fold(this) { current, (replaced, replacement) -> current.replace(replaced, replacement) }
    return when {
        removedPrefixes.any { clean.startsWith(it) } -> clean.substringAfterLast('.')
        else -> clean
    }
}

/**
 * Returns the simple name of the called function for this [IrFunctionAccessExpression].
 *
 * Useful for logging, debugging, and token generation.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrFunctionAccessExpression.simpleFunctionName(): String = symbol.owner.name.asString()

/**
 * Builds an IR statement using [IrBlockBodyBuilder] tied to the given function and expression.
 *
 * This function helps create IR blocks inside plugin transformations in a scoped and safe way.
 *
 * @param pluginContext the current plugin context
 * @param functionToAlign the function under transformation
 * @param expression the source IR element
 * @param body the block-building lambda
 * @return the constructed IR element
 */
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

/**
 * Returns `true` if this [IrFunction] is abstract (declared but not implemented).
 */
val IrFunction.isAbstract get() = this is IrSimpleFunction && modality == Modality.ABSTRACT

/**
 * Returns `true` if this [IrFunction] is concrete (i.e., implemented, not abstract).
 */
val IrFunction.isConcrete get() = !isAbstract
