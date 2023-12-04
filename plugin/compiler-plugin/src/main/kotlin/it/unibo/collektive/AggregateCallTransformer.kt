package it.unibo.collektive

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

typealias AlignedData = Map<String, Int>

/**
 * Looking for the aggregate function call, which is the one that contains the function calls
 * and the branches that have to be aligned. The body of this function call will be
 * transformed by adding the alignedOn function when necessary.
 */
class AggregateCallTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val aggregateContextClass: IrClass,
    private val alignedOnFunction: IrFunction,
) : IrElementTransformerVoid() {

    private val aggregateContext = aggregateContextClass.thisReceiver?.type

    override fun visitFunction(declaration: IrFunction): IrStatement {
        val isAggregate = generateSequence<IrElement>(declaration) { (it as? IrDeclaration)?.parent }
            .any { declaration.extensionReceiverParameter?.type == aggregateContext }
        if (isAggregate) {
            declaration.transformChildren(
                AlignmentTransformer(pluginContext, logger, aggregateContextClass, declaration, alignedOnFunction),
                null,
            )
        }
        return super.visitFunction(declaration)
    }
}
