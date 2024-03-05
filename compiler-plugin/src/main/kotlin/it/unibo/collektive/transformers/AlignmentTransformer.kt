package it.unibo.collektive.transformers

import it.unibo.collektive.utils.branch.addBranchAlignment
import it.unibo.collektive.utils.call.buildAlignedOnCall
import it.unibo.collektive.utils.common.AggregateFunctionNames
import it.unibo.collektive.utils.common.getAlignmentToken
import it.unibo.collektive.utils.common.irStatement
import it.unibo.collektive.utils.common.isAssignableFrom
import it.unibo.collektive.utils.common.simpleFunctionName
import it.unibo.collektive.utils.stack.StackFunctionCall
import it.unibo.collektive.visitors.collectAggregateReference
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

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
) : IrElementTransformer<StackFunctionCall> {
    private var alignedFunctions = emptyMap<String, Int>()

    override fun visitCall(expression: IrCall, data: StackFunctionCall): IrElement {
        val contextReference = expression.receiverAndArgs()
            .find { it.type.isAssignableFrom(aggregateContextClass.defaultType) }
            ?: collectAggregateReference(aggregateContextClass, expression.symbol.owner)

        val alignmentToken = expression.getAlignmentToken()
        if (contextReference == null) {
            data.push(alignmentToken)
        }
        return contextReference?.let { context ->
            // We don't want to align the alignedOn function :)
            if (expression.simpleFunctionName() == AggregateFunctionNames.ALIGNED_ON_FUNCTION)
                return super.visitCall(expression, data)
            // If no function, the first time the counter is 1
            val actualCounter = alignedFunctions[alignmentToken]?.let { it + 1 } ?: 1
            alignedFunctions += alignmentToken to actualCounter

            // If the expression contains a lambda, this recursion is necessary to visit the children
            expression.transformChildren(this, StackFunctionCall())

            irStatement(pluginContext, aggregateLambdaBody, expression) {
                with(logger) {
                    buildAlignedOnCall(
                        pluginContext,
                        aggregateLambdaBody,
                        context,
                        alignedOnFunction,
                        expression,
                        data,
                        alignedFunctions,
                    )
                }
            }
        } ?: super.visitCall(expression, data)
    }

    override fun visitBranch(branch: IrBranch, data: StackFunctionCall): IrBranch {
        with(logger) {
            branch.addBranchAlignment(pluginContext, aggregateContextClass, aggregateLambdaBody, alignedOnFunction)
        }
        return super.visitBranch(branch, data)
    }

    override fun visitElseBranch(branch: IrElseBranch, data: StackFunctionCall): IrElseBranch {
        with(logger) {
            branch.addBranchAlignment(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction,
                false
            )
        }
        return super.visitElseBranch(branch, data)
    }
}