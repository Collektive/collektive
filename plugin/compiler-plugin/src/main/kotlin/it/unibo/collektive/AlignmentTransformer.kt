package it.unibo.collektive

import it.unibo.collektive.utils.branch.addAlignmentToBranchBlock
import it.unibo.collektive.utils.branch.addAlignmentToBranchExpression
import it.unibo.collektive.utils.call.buildAlignedOnCall
import it.unibo.collektive.utils.statement.irStatement
import it.unibo.collektive.visitors.collectAggregateContextReference
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBlock
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
    private val aggregateContextClass: IrClass,
    private val aggregateLambdaBody: IrFunction,
    private val alignedOnFunction: IrFunction,
) : IrElementTransformerVoid() {

    private var aligned: AlignedData = emptyMap()

    override fun visitCall(expression: IrCall): IrExpression {
        val contextReference = expression.receiverAndArgs().find { it.type == aggregateContextClass.defaultType }
            ?: collectAggregateContextReference(aggregateContextClass, expression.symbol.owner)

        return contextReference?.let { context ->
            val functionName = expression.symbol.owner.name.asString()

            // If no function, the first time the counter is 0
            val actualCounter = aligned[functionName]?.let { it + 1 } ?: 1
            aligned += functionName to actualCounter

            // If the expression contains a lambda, this recursion is necessary to visit the children
            expression.transformChildren(this, null)

            irStatement(pluginContext, aggregateLambdaBody, expression) {
                buildAlignedOnCall(pluginContext, aggregateLambdaBody, context, alignedOnFunction, expression, aligned)
            }
        } ?: super.visitCall(expression)
    }

    override fun visitBranch(branch: IrBranch): IrBranch {
        when (branch.result) {
            is IrBlock -> branch.addAlignmentToBranchBlock(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction,
            )

            else -> branch.addAlignmentToBranchExpression(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction,
            )
        }
        return super.visitBranch(branch)
    }

    override fun visitElseBranch(branch: IrElseBranch): IrElseBranch {
        if (branch.result is IrBlock) {
            branch.addAlignmentToBranchBlock(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction,
                conditionValue = false,
            )
        } else {
            branch.addAlignmentToBranchExpression(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction,
                conditionValue = false,
            )
        }
        return super.visitElseBranch(branch)
    }
}
