package it.unibo.collektive.utils.common

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.builtins.StandardNames
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
internal fun IrFunctionAccessExpression.putValueArgument(argument: IrExpression) {
    putValueArgument(
        0,
        argument,
    )
}

/**
 * Set the type argument and the type.
 */
internal fun IrFunctionAccessExpression.putTypeArgument(
    type: IrType,
) {
    this.type = type
    putTypeArgument(0, type)
}

/**
 * Get the value argument in the last index available of the function call.
 */
internal fun IrCall.getLastValueArgument(): IrExpression? = this.getValueArgument(this.valueArgumentsCount - 1)

/**
 * Looking in the receiver and args of a IrCall if there is one that matches the
 * type of class passed as argument.
 */
internal fun IrCall.receiverAndArgs(classToMatch: IrClass): IrExpression? = this.receiverAndArgs().find { it.type == classToMatch.defaultType }

internal fun getLambdaType(
    pluginContext: IrPluginContext,
    lambda: IrSimpleFunction,
): IrType {
    val classFqn = StandardNames.getFunctionClassId(lambda.valueParameters.size).asSingleFqName()
    val base: IrClassSymbol = pluginContext.referenceClass(ClassId(classFqn.parent(), classFqn.shortName()))
        ?: error("function type not found")
    return base.typeWith(lambda.valueParameters.map { it.type } + lambda.returnType)
}
