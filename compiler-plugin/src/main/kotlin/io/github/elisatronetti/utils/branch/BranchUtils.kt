package io.github.elisatronetti.utils.branch

import io.github.elisatronetti.collectAggregateContextReference
import io.github.elisatronetti.utils.statement.irStatement
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*

fun IrBranch.addAlignmentToBranchBlock(
    pluginContext: IrPluginContext,
    aggregateClass: IrClass,
    aggregateLambdaBody: IrSimpleFunction,
    alignOnFunction: IrFunction,
    conditionValue: Boolean = true
) {
    val aggregateRef: IrExpression? = (this.result as IrBlock).findAggregateReference(aggregateClass)
    if (aggregateRef != null) {
        this.result = irStatement(
            pluginContext,
            aggregateLambdaBody,
            this
        ) {
            buildAlignOnBlock(
                pluginContext,
                alignOnFunction,
                this@addAlignmentToBranchBlock,
                conditionValue,
                aggregateRef
            )
        }
    }
}


fun IrBranch.addAlignmentToBranchExpression(
    pluginContext: IrPluginContext,
    aggregateClass: IrClass,
    aggregateLambdaBody: IrSimpleFunction,
    alignOnFunction: IrFunction,
    conditionValue: Boolean = true
) {
    val aggregateRefs: IrExpression? = this.result.findAggregateReference(aggregateClass)
    if (aggregateRefs != null) {
        this.result = irStatement(
            pluginContext,
            aggregateLambdaBody,
            this
        ) {
            buildAlignOnCall(
                pluginContext,
                alignOnFunction,
                this@addAlignmentToBranchExpression,
                conditionValue,
                aggregateRefs
            )
        }
    }
}

private fun IrBlock.findAggregateReference(
    aggregateClass: IrClass
): IrExpression? {
    val aggregateRefs: MutableList<IrExpression?> = mutableListOf()
    val statements = this.statements
    for (statement in statements) {
        if (statement is IrCall) {
            aggregateRefs.add(collectAggregateContextReference(aggregateClass, statement.symbol.owner))
        } else if (statement is IrTypeOperatorCall) {
            aggregateRefs.add(collectAggregateContextReference(aggregateClass, statement))
        }
    }
    return aggregateRefs.filterNotNull().firstOrNull()
}

private fun IrExpression.findAggregateReference(
    aggregateClass: IrClass
): IrExpression? = collectAggregateContextReference(aggregateClass, this)
