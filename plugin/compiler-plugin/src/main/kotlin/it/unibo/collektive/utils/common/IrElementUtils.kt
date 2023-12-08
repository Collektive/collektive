package it.unibo.collektive.utils.common

import it.unibo.collektive.utils.logging.error
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.name.ClassId

/**
 * Put a value argument at the head of the function's arguments.
 */
internal fun IrFunctionAccessExpression.putValueArgument(valueArgument: IrExpression) =
    putValueArgument(0, valueArgument)

/**
 * Set the type argument and the type.
 */
internal fun IrFunctionAccessExpression.putTypeArgument(typeArgument: IrType) {
    type = typeArgument
    putTypeArgument(0, typeArgument)
}

/**
 * Get the value argument in the last index available of the function call.
 */
internal fun IrCall.getLastValueArgument(): IrExpression? = getValueArgument(valueArgumentsCount - 1)

/**
 * Looking in the receiver and args of a IrCall if there is one that matches the
 * type of class passed as argument.
 */
internal fun IrCall.receiverAndArgs(classToMatch: IrClass): IrExpression? =
    receiverAndArgs().find { it.type == classToMatch.defaultType }

context(MessageCollector)
internal fun getLambdaType(pluginContext: IrPluginContext, lambda: IrSimpleFunction): IrType {
    val classFqn = StandardNames.getFunctionClassId(lambda.valueParameters.size).asSingleFqName()
    return pluginContext.referenceClass(ClassId(classFqn.parent(), classFqn.shortName()))?.let { base ->
        base.typeWith(lambda.valueParameters.map { it.type } + lambda.returnType)
    } ?: run {
        error("""
            Unable to reference the class ${classFqn.parent()}.
            This is may due to a bug in collektive compiler plugin.
            """.trimIndent())
        throw ClassNotFoundException("Unable to reference ${classFqn.parent()}")
    }
}
