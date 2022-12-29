package io.github.elisatronetti.visitors

import io.github.elisatronetti.utils.common.Name
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

    // Visit all the children of the root element
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    // Looking for the function declaration that matches the target's name
    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        if (declaration.name.toString() == Name.ALIGNED_ON_FUNCTION){
            elements.add(declaration)
        }
        super.visitFunction(declaration, data)
    }
}

/**
 * Retrieve the first function that matches the target's function name or null.
 */
fun collectAlignedOnFunction(element: IrElement): IrFunction? =
    buildList {
        element.accept(AlignmentIrElementVisitor(this), null)
    }.firstOrNull()
