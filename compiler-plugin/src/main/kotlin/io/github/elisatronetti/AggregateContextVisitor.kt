package io.github.elisatronetti

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

/**
 * Class that visit all the children of the IR, looking for the
 * AggregateContext class.
 */
class StackClassVisitor(
    private val elements: MutableList<IrClass>
) : IrElementVisitor<Unit, Nothing?> {
    private val aggregateContextClassName: String = "AggregateContext"

    // Visit all the children of the root element
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    // Visit all the classes looking for the AggregateContext class
    override fun visitClass(declaration: IrClass, data: Nothing?) {
        if (declaration.name.asString() == aggregateContextClassName) {
            elements.add(declaration)
        }
        super.visitClass(declaration, data)
    }

}

/**
 * Retrieve the classes that match the target class name.
 */
fun collectClass(element: IrElement) = buildList {
    element.accept(StackClassVisitor(this), null)
}
