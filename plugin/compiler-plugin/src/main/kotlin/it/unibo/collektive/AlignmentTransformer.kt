package it.unibo.collektive

import it.unibo.collektive.utils.branch.addBranchAlignment
import it.unibo.collektive.utils.call.buildAlignedOnCall
import it.unibo.collektive.utils.common.AggregateFunctionNames.ALIGNED_ON_FUNCTION
import it.unibo.collektive.utils.common.getFunctionName
import it.unibo.collektive.utils.common.isAssignableFrom
import it.unibo.collektive.utils.statement.irStatement
import it.unibo.collektive.visitors.collectAggregateReference
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * This transforms the generated IR only when an aggregate computing's function is involved:
 * for each function call and branch found, they are going to be wrapped in the alignedOn
 * function.
 */
class AlignmentTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val aggregateContextClass: IrClass,
    private val aggregateLambdaBody: IrFunction,
    private val alignedOnFunction: IrFunction,
) : IrElementTransformerVoid() {
    private var alignedFunctions: AlignedData = emptyMap()

    override fun visitCall(expression: IrCall): IrExpression {
        val contextReference = expression.receiverAndArgs()
            .find { it.type.isAssignableFrom(aggregateContextClass.defaultType) }
            ?: collectAggregateReference(aggregateContextClass, expression.symbol.owner)
        return contextReference?.let { context ->
            val functionName = expression.getFunctionName()
            // We don't want to align the alignedOn function :)
            if (functionName == ALIGNED_ON_FUNCTION) return super.visitCall(expression)

            // If no function, the first time the counter is 1
            val actualCounter = alignedFunctions[functionName]?.let { it + 1 } ?: 1
            alignedFunctions += functionName to actualCounter

            // If the expression contains a lambda, this recursion is necessary to visit the children
            expression.transformChildren(this, null)

            irStatement(pluginContext, aggregateLambdaBody, expression) {
                with(logger) {
                    buildAlignedOnCall(
                        pluginContext,
                        aggregateLambdaBody,
                        context,
                        alignedOnFunction,
                        expression,
                        alignedFunctions,
                    )
                }
            }
        } ?: super.visitCall(expression)
    }

    override fun visitBranch(branch: IrBranch): IrBranch {
        with(logger) {
            branch.addBranchAlignment(pluginContext, aggregateContextClass, aggregateLambdaBody, alignedOnFunction)
        }
        return super.visitBranch(branch)
    }

    override fun visitElseBranch(branch: IrElseBranch): IrElseBranch {
        with(logger) {
            branch.addBranchAlignment(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction,
                false
            )
        }
        return super.visitElseBranch(branch)
    }
}
