/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.backend.irextensions

import it.unibo.collektive.compiler.backend.util.withBetterSymbols
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.receiverAndArgs

/**
 * Returns `true` if this [IrFunctionAccessExpression] is a getter call.
 *
 * Recognizes synthetic Kotlin getter functions by their mangled name,
 * which typically start with `<get-`.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
internal val IrFunctionAccessExpression.isGetter: Boolean
    get() = this is IrCall && symbol.owner.kotlinFqName.asString().substringAfterLast('.').startsWith("<get-")

/**
 * Returns the unmangled simple name of the function called by this [IrFunctionAccessExpression].
 *
 * Useful for logging, debugging, and token generation.
 */
@OptIn(UnsafeDuringIrConstructionAPI::class)
internal val IrFunctionAccessExpression.simpleFunctionName: String
    get() = symbol.owner.name.asString()

/**
 * Computes a unique token that identifies this [IrFunctionAccessExpression]
 * for alignment purposes.
 *
 * The token includes:
 * - the fully qualified name of the function or constructor
 * - any type arguments (generic parameters)
 * - the types of the receiver and all arguments
 *
 * @receiver the function or constructor call
 * @return a string token uniquely representing the call's semantics
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
            val property = ownerName.substringAfterLast('<')
            val propertyNiceName = property.substring(4..(property.length - 2))
            val receiver = receiverAndArgs().singleOrNull()
            when (receiver) {
                null -> {
                    // Top level property or delegated property
                    val isDelegated =
                        (symbol.owner as? IrSimpleFunction)?.correspondingPropertySymbol?.owner?.isDelegated == true
                    if (isDelegated) "by { $propertyNiceName }" else propertyNiceName
                }
                else -> {
                    val receiverName = receiver.type.classFqName?.asString()?.withBetterSymbols() ?: "anonymous-type"
                    "$receiverName.$propertyNiceName"
                }
            }
        }
        symbolOwner.name.isSpecial -> "λ$generics$arguments"
        else -> symbolOwner.kotlinFqName.asString().withBetterSymbols() + generics + arguments
    }
}
