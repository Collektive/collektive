package it.unibo.collektive.visitors

import it.unibo.collektive.utils.common.isAssignableFrom
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.receiverAndArgs
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

/**
 * Class that visit all the children of the IR, looking for the
 * AggregateContext class.
 */
private class AggregateRefChildrenVisitor(
    private val aggregateContextClass: IrClass,
    private val elements: MutableList<IrExpression>,
) : IrElementVisitor<Unit, Nothing?> {
    // Visit all the children of the root element
    override fun visitElement(
        element: IrElement,
        data: Nothing?,
    ) {
        element.acceptChildren(this, data)
    }

    // Search in each call if in its receiver or arguments there is the reference to
    // the aggregate context
    override fun visitCall(
        expression: IrCall,
        data: Nothing?,
    ) {
        val aggregateContextRef =
            expression
                .receiverAndArgs()
                .find { it.type.isAssignableFrom(aggregateContextClass.defaultType) }
        aggregateContextRef?.let { elements.add(it) } ?: super.visitCall(expression, data)
    }
}

/**
 * Retrieve the aggregate context reference by looking in all the function call in the element found.
 */
fun collectAggregateReference(
    aggregateContextClass: IrClass,
    element: IrElement,
): IrExpression? =
    buildList { element.accept(AggregateRefChildrenVisitor(aggregateContextClass, this), null) }
        .firstOrNull()
