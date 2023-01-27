package io.github.elisatronetti.utils.branch

import io.github.elisatronetti.utils.statement.irStatement
import io.github.elisatronetti.visitors.collectAggregateContextReference
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*

fun IrBranch.addAlignmentToBranchBlock(
    pluginContext: IrPluginContext,
    aggregateContextClass: IrClass,
    aggregateLambdaBody: IrFunction,
    alignedOnFunction: IrFunction,
    conditionValue: Boolean = true
) {
    val aggregateContextReference: IrExpression? = (this.result as IrBlock).findAggregateReference(aggregateContextClass)
    if (aggregateContextReference != null) {
        this.result = irStatement(
            pluginContext,
            aggregateLambdaBody,
            this
        ) {
            buildAlignedOnBlock(
                pluginContext,
                aggregateContextReference,
                alignedOnFunction,
                this@addAlignmentToBranchBlock,
                conditionValue
            )
        }
    }
}


fun IrBranch.addAlignmentToBranchExpression(
    pluginContext: IrPluginContext,
    aggregateContextClass: IrClass,
    aggregateLambdaBody: IrFunction,
    alignedOnFunction: IrFunction,
    conditionValue: Boolean = true
) {
    val aggregateContextReference: IrExpression? = this.result.findAggregateReference(aggregateContextClass)
    if (aggregateContextReference != null) {
        this.result = irStatement(
            pluginContext,
            aggregateLambdaBody,
            this
        ) {
            buildAlignedOnCall(
                pluginContext,
                aggregateContextReference,
                alignedOnFunction,
                this@addAlignmentToBranchExpression,
                conditionValue
            )
        }
    }
}

private fun IrBlock.findAggregateReference(
    aggregateContextClass: IrClass
): IrExpression? {
    val aggregateContextReferences: MutableList<IrExpression?> = mutableListOf()
    val statements = this.statements
    for (statement in statements) {
        if (statement is IrCall) {
            val callAggregateReference = collectAggregateContextReference(aggregateContextClass, statement)
            if (callAggregateReference == null) {
                aggregateContextReferences.add(
                    collectAggregateContextReference(aggregateContextClass, statement.symbol.owner)
                )
            } else {
                aggregateContextReferences.add(callAggregateReference)
            }
        } else if (statement is IrTypeOperatorCall) {
            aggregateContextReferences.add(collectAggregateContextReference(aggregateContextClass, statement))
        }
    }
    return aggregateContextReferences.filterNotNull().firstOrNull()
}

private fun IrExpression.findAggregateReference(
    aggregateContextClass: IrClass
): IrExpression? =
    when (this) {
        is IrCall -> {
            collectAggregateContextReference(aggregateContextClass, this) ?: collectAggregateContextReference(
                aggregateContextClass,
                this.symbol.owner
            )
        }
        else -> collectAggregateContextReference(aggregateContextClass, this)
    }
