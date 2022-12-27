package io.github.elisatronetti.utils.branch

import io.github.elisatronetti.collectAggregateReference
import io.github.elisatronetti.utils.irStatement
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
    val aggregateRefs = (this.result as IrBlock).findAggregateReferences(aggregateClass)
    if (aggregateRefs.isNotEmpty()) {
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
                aggregateRefs.first()
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
    val aggregateRefs = this.result.findAggregateReferences(aggregateClass)
    if (aggregateRefs.isNotEmpty()) {
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
                aggregateRefs.first()
            )
        }
    }
}

private fun IrBlock.findAggregateReferences(
    aggregateClass: IrClass
): List<IrExpression> {
    val aggregateRefs: MutableList<IrExpression> = mutableListOf()
    val statements = this.statements
    for (statement in statements) {
        if (statement is IrCall || statement is IrTypeOperatorCall){
            aggregateRefs.addAll(collectAggregateReference(aggregateClass, statement))
        }
    }
    return aggregateRefs.toList()
}

private fun IrExpression.findAggregateReferences(
    aggregateClass: IrClass
): List<IrExpression> =  collectAggregateReference(aggregateClass, this)
