package io.github.elisatronetti

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

/**
 * Class that visit all the children of the aggregate, looking for the all the
 * function calls.
 */
class AggregateCallVisitor(
        private val elements: MutableList<IrCall>
) : IrElementVisitor<Unit, Nothing?> {

    // Visit all the children of the root element
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    override fun visitCall(expression: IrCall, data: Nothing?) {
        if (elements.find { it.symbol.owner.name != expression.symbol.owner.name } == null) {
            elements.add(expression)
        }
        //if (expression !in elements) elements.add(expression)
        super.visitCall(expression, data)
    }
}

/**
 * Retrieve the function calls found in aggregate.
 */
fun collectCalls(element: IrElement) = buildList {
    element.accept(AggregateCallVisitor(this), null)
}
