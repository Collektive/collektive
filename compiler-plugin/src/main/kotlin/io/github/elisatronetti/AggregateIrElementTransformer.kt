package io.github.elisatronetti

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * Looking for the aggregate function call, find all its function call
 * children and transform them.
 */
class AggregateIrElementTransformer(
    private val pluginContext: IrPluginContext,
    private val alignOnFunction: IrFunction,
    private val aggregateContext: IrClass
) : IrElementTransformerVoid() {
    private val aggregateFunctionCallName: String = "aggregate"

    override fun visitCall(expression: IrCall): IrExpression {
        if (expression.symbol.owner.name.asString() == aggregateFunctionCallName) {
            val aggregateLambdaBody = (expression.getValueArgument(expression.valueArgumentsCount - 1) as IrFunctionExpression).function
            collectCalls(expression).forEach {
                // Modify all irCall that are children of aggregate
                it.transform(
                    AlignmentIrElementTransformer(pluginContext, alignOnFunction, aggregateLambdaBody, aggregateContext),
                    null
                )
            }
        }
        return super.visitCall(expression)
    }
}
