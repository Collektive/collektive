package io.github.elisatronetti.utils.common

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.allParameters
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith

/**
 * Put a value argument at the head of the function's arguments.
 */
internal fun IrFunctionAccessExpression.putValueArgument(
    argument: IrExpression
){
    putValueArgument(
        0,
        argument
    )
}

/**
 * Set the type argument and the type.
 */
internal fun IrFunctionAccessExpression.putTypeArgument(
    type: IrType
){
    this.type = type
    putTypeArgument(0, type)
}

internal fun getLambdaType(
    pluginContext: IrPluginContext,
    lambda: IrSimpleFunction
): IrType {
    val base = pluginContext.referenceClass(
        StandardNames.getFunctionClassId(lambda.allParameters.size).asSingleFqName()
    )
        ?: error("function type not found")
    return base.typeWith(lambda.allParameters.map { it.type } + lambda.returnType)
}
