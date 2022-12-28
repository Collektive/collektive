package io.github.elisatronetti

import io.github.elisatronetti.utils.common.Name
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

    // Visit all the children of the root element
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    // Visit all the classes looking for the AggregateContext class
    override fun visitClass(declaration: IrClass, data: Nothing?) {
        if (declaration.name.asString() == Name.AGGREGATE_CONTEXT_CLASS) {
            elements.add(declaration)
        }
        super.visitClass(declaration, data)
    }

}

/**
 * Retrieve the first class that matches the target's class name or null.
 */
fun collectAggregateContextClass(element: IrElement): IrClass? = buildList {
    element.accept(StackClassVisitor(this), null)
}.firstOrNull()
