package io.github.elisatronetti

import io.github.elisatronetti.utils.common.Name
import io.github.elisatronetti.utils.common.getLastValueArgument
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * Looking for the aggregate function call, which is the one that contains the function calls
 * and the branches that have to be aligned. The body of this function call will be
 * transformed by adding the alignedOn function when necessary.
 */
class AggregateCallTransformer(
    private val pluginContext: IrPluginContext,
    private val aggregateContextClass: IrClass,
    private val alignedOnFunction: IrFunction
) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {
        if (expression.symbol.owner.name.asString() == Name.AGGREGATE_FUNCTION) {
            val aggregateLambdaBody =
                (expression.getLastValueArgument() as IrFunctionExpression).function
            expression.transform(
                AlignmentIrElementTransformer(
                    pluginContext,
                    aggregateContextClass,
                    aggregateLambdaBody,
                    alignedOnFunction
                ),
                null
            )
        }
        return super.visitCall(expression)
    }
}
