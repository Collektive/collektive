package it.unibo.collektive

import it.unibo.collektive.utils.common.AggregateFunctionNames
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import it.unibo.collektive.utils.call.buildAlignedOnCall
import it.unibo.collektive.utils.common.receiverAndArgs
import it.unibo.collektive.utils.statement.irStatement
import it.unibo.collektive.visitors.collectAggregateContextReference
import it.unibo.collektive.utils.branch.addAlignmentToBranchBlock
import it.unibo.collektive.utils.branch.addAlignmentToBranchExpression

/**
 * This transforms the generated IR only when an aggregate computing's function is involved:
 * for each function call and branch found, they are going to be wrapped in the alignedOn
 * function.
 */
class AlignmentTransformer(
    private val pluginContext: IrPluginContext,
    private val aggregateContextClass: IrClass,
    private val aggregateLambdaBody: IrFunction,
    private val alignedOnFunction: IrFunction
) : IrElementTransformerVoid() {

   override fun visitCall(expression: IrCall): IrExpression {
       if (expression.symbol.owner.name.asString() == AggregateFunctionNames.ALIGNED_ON_FUNCTION) return super.visitCall(expression)

       val aggregateContextReference: IrExpression =
           expression.receiverAndArgs(aggregateContextClass)
               ?: (collectAggregateContextReference(aggregateContextClass, expression.symbol.owner)
                   ?: return super.visitCall(expression))

       // If the expression contains a lambda, this recursion is necessary to visit the children
       expression.transformChildren(
           AlignmentTransformer(
               pluginContext,
               aggregateContextClass,
               aggregateLambdaBody,
               alignedOnFunction
           ),
           null
       )
       return irStatement(
           pluginContext,
           aggregateLambdaBody,
           expression
       ) {
           buildAlignedOnCall(
               pluginContext,
               aggregateLambdaBody,
               aggregateContextReference,
               alignedOnFunction,
               expression
           )
       }
    }

    override fun visitBranch(branch: IrBranch): IrBranch {
        when (branch.result) {
            is IrBlock -> branch.addAlignmentToBranchBlock(pluginContext, aggregateContextClass, aggregateLambdaBody, alignedOnFunction)
            else -> branch.addAlignmentToBranchExpression(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction
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
                conditionValue = false
            )
        } else {
            branch.addAlignmentToBranchExpression(
                pluginContext,
                aggregateContextClass,
                aggregateLambdaBody,
                alignedOnFunction,
                conditionValue = false
            )
        }
        return super.visitElseBranch(branch)
    }
}
