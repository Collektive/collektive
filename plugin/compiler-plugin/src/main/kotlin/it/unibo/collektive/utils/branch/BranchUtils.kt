package it.unibo.collektive.utils.branch

import it.unibo.collektive.utils.statement.irStatement
import it.unibo.collektive.visitors.collectAggregateContextReference
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall

context(MessageCollector)
fun IrBranch.addBranchAlignment(
    pluginContext: IrPluginContext,
    aggregateContextClass: IrClass,
    aggregateLambdaBody: IrFunction,
    alignedOnFunction: IrFunction,
    conditionValue: Boolean = true,
) {
    result.findAggregateReference(aggregateContextClass)?.let {
        result = irStatement(pluginContext, aggregateLambdaBody, this) {
            buildAlignedOn(pluginContext, it, alignedOnFunction, this@addBranchAlignment, conditionValue)
        }
    }
}

private fun IrBlock.findAggregateReference(aggregateContextClass: IrClass): IrExpression? {
    val aggregateContextReferences: MutableList<IrExpression?> = mutableListOf()
    for (statement in statements) {
        when (statement) {
            is IrCall -> collectAggregateContextReference(aggregateContextClass, statement)
                ?.let { aggregateContextReferences.add(it) }
                ?: aggregateContextReferences.add(
                    collectAggregateContextReference(aggregateContextClass, statement.symbol.owner),
                )
            is IrTypeOperatorCall ->
                aggregateContextReferences.add(collectAggregateContextReference(aggregateContextClass, statement))
        }
    }
    return aggregateContextReferences.filterNotNull().firstOrNull()
}

private fun IrExpression.findAggregateReference(aggregateContextClass: IrClass): IrExpression? = when (this) {
    is IrBlock -> findAggregateReference(aggregateContextClass)
    is IrCall -> collectAggregateContextReference(aggregateContextClass, this)
        ?: collectAggregateContextReference(aggregateContextClass, symbol.owner)
    else -> collectAggregateContextReference(aggregateContextClass, this)
}
