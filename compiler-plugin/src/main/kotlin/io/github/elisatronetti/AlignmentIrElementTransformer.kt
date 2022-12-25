package io.github.elisatronetti

import io.github.elisatronetti.utils.buildAlignOnBlock
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import io.github.elisatronetti.utils.buildAlignOnCall

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
        if (expression.symbol.owner.name.asString() == "alignedOn") return super.visitCall(expression)
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

        return irStatement(expression) {
            buildAlignOnCall(pluginContext, aggregateLambdaBody, alignOnFunction, expression, aggregateContext)
        }
    }

    override fun visitBranch(branch: IrBranch): IrBranch {
        val aggregateRefs: MutableList<IrExpression> = mutableListOf()
        if (branch.result is IrBlock) {
            val statements = (branch.result as IrBlock).statements
            for (statement in statements) {
                if (statement is IrCall || statement is IrTypeOperatorCall){
                     aggregateRefs.addAll(collectAggregateReference(aggregateClass, statement))
                }
            }
        } else {
            aggregateRefs.addAll(collectAggregateReference(aggregateClass, branch.result))
        }
        if (aggregateRefs.isNotEmpty()) {
            branch.result = irStatement(branch) {
                if (branch.result is IrBlock){
                    buildAlignOnBlock(pluginContext, alignOnFunction, branch, true, aggregateRefs.first())
                } else {
                    buildAlignOnCall(pluginContext, alignOnFunction, branch, true, aggregateRefs.first())
                }
            }
        }
        return super.visitBranch(branch)
    }


    private fun <T : IrElement> irStatement(expression: IrBranch, body: IrSingleStatementBuilder.() -> T): T =
        IrSingleStatementBuilder(
            pluginContext,
            Scope(aggregateLambdaBody.symbol),
            expression.startOffset,
            expression.endOffset
        ).build(body)

    private fun <T : IrElement> irStatement(expression: IrCall, body: IrSingleStatementBuilder.() -> T): T =
        IrSingleStatementBuilder(
            pluginContext,
            Scope(expression.symbol),
            expression.startOffset,
            expression.endOffset
        ).build(body)
}
