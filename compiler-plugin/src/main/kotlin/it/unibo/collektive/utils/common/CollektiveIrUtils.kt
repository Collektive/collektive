package it.unibo.collektive.utils.common

import it.unibo.collektive.utils.logging.error
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId

internal fun IrType.isAssignableFrom(other: IrType): Boolean = classifierOrNull?.let { base ->
    other.classifierOrNull?.let { other ->
        FqNameEqualityChecker.areEqual(base, other)
    } ?: false
} ?: false

context(MessageCollector)
internal fun getLambdaType(pluginContext: IrPluginContext, lambda: IrSimpleFunction): IrType {
    val classFqn = StandardNames.getFunctionClassId(lambda.valueParameters.size).asSingleFqName()
    val type = pluginContext.referenceClass(ClassId(classFqn.parent(), classFqn.shortName()))?.let { base ->
        base.typeWith(lambda.valueParameters.map { it.type } + lambda.returnType)
    }
    requireNotNull(type) {
        """
        Unable to reference the class ${classFqn.parent()}.
        This is may due to a bug in collektive compiler plugin.
        """.trimIndent().also {
            error(it)
        }
    }
    return type
}

internal fun List<IrType?>.stringified(
    prefix: String = "(",
    postfix: String = ")",
): String = joinToString(",", prefix = prefix, postfix = postfix) {
    it?.classFqName?.asString() ?: "?"
}

internal fun IrCall.getAlignmentToken(): String {
    val symbolOwner = symbol.owner
    val arguments = receiverAndArgs().map { it.type }.stringified()
    val generics = typeArguments.stringified("<", ">")
    return when {
        symbolOwner.name.isSpecial -> "λ"
        else -> symbolOwner.kotlinFqName.asString() + generics + arguments
    }
}

internal fun IrCall.simpleFunctionName(): String = symbol.owner.name.asString()

internal fun <T : IrElement> irStatement(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrFunction,
    expression: IrElement,
    body: IrSingleStatementBuilder.() -> T,
): T = IrSingleStatementBuilder(
    pluginContext,
    Scope(aggregateLambdaBody.symbol),
    expression.startOffset,
    expression.endOffset,
).build(body)
