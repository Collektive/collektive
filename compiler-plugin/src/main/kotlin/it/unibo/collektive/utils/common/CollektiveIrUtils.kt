package it.unibo.collektive.utils.common

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
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

internal fun IrType.isAssignableFrom(other: IrType): Boolean =
    classifierOrNull?.let { base ->
        other.classifierOrNull?.let { other ->
            FqNameEqualityChecker.areEqual(base, other)
        } ?: false
    } ?: false

internal fun List<IrType?>.stringified(
    prefix: String = "(",
    postfix: String = ")",
): String =
    joinToString(",", prefix = prefix, postfix = postfix) {
        it?.classFqName?.asString() ?: "?"
    }

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrCall.getAlignmentToken(): String {
    val symbolOwner = symbol.owner
    val arguments = receiverAndArgs().map { it.type }.stringified()
    val generics = typeArguments.stringified("<", ">")
    return when {
        symbolOwner.name.isSpecial -> "λ"
        else -> symbolOwner.kotlinFqName.asString() + generics + arguments
    }
}

@OptIn(UnsafeDuringIrConstructionAPI::class)
internal fun IrCall.simpleFunctionName(): String = symbol.owner.name.asString()

internal fun <T : IrElement> irStatement(
    pluginContext: IrPluginContext,
    functionToAlign: IrFunction,
    expression: IrElement,
    body: IrBlockBodyBuilder.() -> T,
): T =
    IrBlockBodyBuilder(
        pluginContext,
        Scope(functionToAlign.symbol),
        expression.startOffset,
        expression.endOffset,
    ).body()
