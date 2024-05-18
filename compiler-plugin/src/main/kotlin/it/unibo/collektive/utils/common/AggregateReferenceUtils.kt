package it.unibo.collektive.utils.common

import it.unibo.collektive.visitors.collectAggregateReference
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.IrWhen

private fun IrBlock.findAggregateReference(aggregateContextClass: IrClass): IrExpression? =
    statements.firstNotNullOfOrNull {
        when (it) {
            is IrCall -> collectAggregateReference(aggregateContextClass, it)
                ?: collectAggregateReference(aggregateContextClass, it.symbol.owner)

            is IrVariable -> collectAggregateReference(aggregateContextClass, it)
            is IrTypeOperatorCall -> collectAggregateReference(aggregateContextClass, it)
            is IrWhen -> collectAggregateReference(aggregateContextClass, it)
            else -> null // collectAggregateReference(aggregateContextClass, it)
        }
    }

internal fun IrExpression.findAggregateReference(aggregateContextClass: IrClass): IrExpression? = when (this) {
    is IrBlock -> findAggregateReference(aggregateContextClass)

    is IrGetValue -> collectAggregateReference(aggregateContextClass, this)
        ?: collectAggregateReference(aggregateContextClass, symbol.owner)

    is IrCall -> collectAggregateReference(aggregateContextClass, this)
        ?: collectAggregateReference(aggregateContextClass, symbol.owner)

    else -> collectAggregateReference(aggregateContextClass, this)
}
