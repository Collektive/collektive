package io.github.elisatronetti

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

/**
 * Class that visit all the children of the IR, looking for the function
 * that is responsible to handle the alignment.
 */
class AlignmentIrElementVisitor(
    private val elements: MutableList<IrFunction>
) : IrElementVisitor<Unit, Nothing?> {
    private val alignRawFunctionName: String = "alignedOn"

    // Visit all the children of the root element
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    // Looking for the function declaration with the target names
    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        if (declaration.name.toString() == alignRawFunctionName){
            elements.add(declaration)
        }
        super.visitFunction(declaration, data)
    }
}

/**
 * Retrieve the function that matches the target function name.
 */
fun collect(element: IrElement) = buildList {
    element.accept(AlignmentIrElementVisitor(this), null)
}
