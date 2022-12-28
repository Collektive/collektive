package io.github.elisatronetti

import io.github.elisatronetti.utils.common.receiverAndArgs
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

/**
 * Class that visit all the children of the IR, looking for the
 * AggregateContext class.
 */
class AggregateRefChildrenVisitor(
    private val aggregateContext: IrClass,
    private val elements: MutableList<IrExpression>
) : IrElementVisitor<Unit, Nothing?> {

    // Visit all the children of the root element
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    override fun visitCall(expression: IrCall, data: Nothing?) {
        val aggregateContextRef: IrExpression? = expression.receiverAndArgs(aggregateContext)
        if (aggregateContextRef != null) {
            elements.add(aggregateContextRef)
        }
        super.visitCall(expression, data)
    }

}

/**
 * Retrieve the classes that match the target class name.
 */
fun collectAggregateReference(aggregateContext: IrClass, element: IrElement) = buildList {
    element.accept(AggregateRefChildrenVisitor(aggregateContext, this), null)
}
