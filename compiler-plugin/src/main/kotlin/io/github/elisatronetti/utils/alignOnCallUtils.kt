package io.github.elisatronetti.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*
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

fun IrSingleStatementBuilder.buildAlignOnCall(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrSimpleFunction,
    alignOnFunction: IrFunction,
    condition: IrExpression,
    conditionValue: Boolean,
    block: IrBlock,
    aggregateContext: IrExpression
): IrContainerExpression {
    return irBlock {
        val call = irCall(alignOnFunction).apply {
            // Set generics type
            val lastExpression = block.statements.last() as IrExpression
            if (lastExpression is IrTypeOperatorCall){
                this.type = lastExpression.argument.type
                putTypeArgument(0, lastExpression.argument.type)
            }
            // Set aggregate context
            putArgument(alignOnFunction.dispatchReceiverParameter!!, aggregateContext)
            // Set the argument that is going to be push in the stack

            putValueArgument(
                0,
                irString(createConditionArgument(condition, conditionValue))
            )
            // Create the lambda that is going to call expression
            val lambda = buildLambdaArgument(pluginContext, aggregateLambdaBody, block)
            putValueArgument(1, lambda)
        }
        +call
        typeOperator(block.type, call, IrTypeOperator.IMPLICIT_COERCION_TO_UNIT, block.type)
    }
}

private fun createConditionArgument(condition: IrExpression, conditionValue: Boolean): String {
    when (condition) {
        is IrGetValue -> return Pair(condition.symbol.owner.name.asString(), conditionValue.toString()).toString()
        is IrConst<*> -> return Pair("constant", conditionValue.toString()).toString()
        is IrCall -> return Pair(condition.symbol.owner.name.asString(), conditionValue.toString()).toString()
    }
    throw IllegalStateException(
        "The current if condition type ${condition::class} has not been handled for the alignment yet. " +
                "Update the compiler plugin."
    )
}
