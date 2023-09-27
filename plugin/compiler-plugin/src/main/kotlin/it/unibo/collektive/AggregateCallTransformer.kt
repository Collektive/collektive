package it.unibo.collektive

import it.unibo.collektive.utils.common.AggregateFunctionNames
import it.unibo.collektive.utils.common.getLastValueArgument
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

typealias AlignedData = Map<String, Int>

/**
 * Looking for the aggregate function call, which is the one that contains the function calls
 * and the branches that have to be aligned. The body of this function call will be
 * transformed by adding the alignedOn function when necessary.
 */
class AggregateCallTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val aggregateContextClass: IrClass,
    private val alignedOnFunction: IrFunction,
) : IrElementTransformerVoid() {

    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (!declaration.name.isSpecial && declaration.extensionReceiverParameter?.type == aggregateContextClass.thisReceiver?.type) {
            declaration.transformChildren(
                AlignmentTransformer(
                    pluginContext,
                    logger,
                    aggregateContextClass,
                    declaration,
                    alignedOnFunction,
                ),
                null,
            )
        }
        return super.visitFunction(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        if (expression.symbol.owner.name.asString() == AggregateFunctionNames.AGGREGATE_FUNCTION) {
            val aggregateLambdaBody =
                (expression.getLastValueArgument() as IrFunctionExpression).function
            expression.transformChildren(
                AlignmentTransformer(
                    pluginContext,
                    logger,
                    aggregateContextClass,
                    aggregateLambdaBody,
                    alignedOnFunction,
                ),
                null,
            )
        }
        return super.visitCall(expression)
    }
}
