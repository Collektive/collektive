package io.github.elisatronetti

import io.github.elisatronetti.utils.common.Name
import io.github.elisatronetti.utils.branch.*
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import io.github.elisatronetti.utils.call.buildAlignOnCall
import io.github.elisatronetti.utils.statement.irStatement

/**
 * This transform the generated IR, creating in the function declaration a new function call,
 * which is responsible to handle the alignment.
 */
class AlignmentIrElementTransformer(
    private val pluginContext: IrPluginContext,
    private val alignOnFunction: IrFunction,
    private val aggregateLambdaBody: IrSimpleFunction,
    private val aggregateClass: IrClass
) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {
        if (expression.symbol.owner.name.asString() == Name.ALIGNED_ON_FUNCTION) return super.visitCall(expression)

        val aggregateContextRef: IrExpression? = expression.receiverAndArgs().find {
            it.type == aggregateClass.defaultType
        }

        val aggregateContext: IrExpression = if (aggregateContextRef != null) {
            aggregateContextRef
        } else {
            // Find the aggregate context looking in all the children of expression
            val childrenAggregateRefs = collectAggregateReference(aggregateClass, expression.symbol.owner)
            if (childrenAggregateRefs.isNotEmpty()) {
                childrenAggregateRefs.first()
            } else {
                return super.visitCall(expression)
            }
        }

        return irStatement(
            pluginContext,
            aggregateLambdaBody,
            expression
        ) {
            buildAlignOnCall(
                pluginContext,
                aggregateLambdaBody,
                alignOnFunction,
                aggregateContext,
                expression
            )
        }
    }

    override fun visitBranch(branch: IrBranch): IrBranch {
        if (branch.result is IrBlock) {
            branch.addAlignmentToBranchBlock(
                pluginContext,
                aggregateClass,
                aggregateLambdaBody,
                alignOnFunction
            )
        } else {
            branch.addAlignmentToBranchExpression(
                pluginContext,
                aggregateClass,
                aggregateLambdaBody,
                alignOnFunction
            )
        }
        return super.visitBranch(branch)
    }

    override fun visitElseBranch(branch: IrElseBranch): IrElseBranch {
        if (branch.result is IrBlock) {
            branch.addAlignmentToBranchBlock(
                pluginContext,
                aggregateClass,
                aggregateLambdaBody,
                alignOnFunction,
                conditionValue = false
            )
        } else {
            branch.addAlignmentToBranchExpression(
                pluginContext,
                aggregateClass,
                aggregateLambdaBody,
                alignOnFunction,
                conditionValue = false
            )
        }
        return super.visitElseBranch(branch)
    }
}
