package io.github.elisatronetti.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.util.kotlinFqName

fun IrSingleStatementBuilder.buildAlignOnCall(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrSimpleFunction,
    alignOnFunction: IrFunction,
    expression: IrCall,
    aggregateContext: IrExpression
): IrFunctionAccessExpression {
    return irCall(alignOnFunction).apply {
        // Set generics type
        type = expression.type
        putTypeArgument(0, expression.type)
        // Set aggregate context
        putArgument(alignOnFunction.dispatchReceiverParameter!!, aggregateContext)
        // Set the argument that is going to be push in the stack
        putValueArgument(
            0,
            irString(expression.symbol.owner.kotlinFqName.asString())
        )
        // Create the lambda that is going to call expression
        val lambda = buildLambdaArgument(pluginContext, aggregateLambdaBody, expression)
        putValueArgument(1, lambda)
    }
}
