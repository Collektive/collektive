package it.unibo.collektive.transformers

import it.unibo.collektive.utils.common.isAssignableFrom
import it.unibo.collektive.utils.stack.StackFunctionCall
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * Looking for the aggregate function call, which is the one that contains the function calls
 * and the branches that have to be aligned. The body of this function call will be
 * transformed by adding the alignedOn function when necessary.
 */
class AggregateCallTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val aggregateClass: IrClass,
    private val alignedOnFunction: IrFunction,
    private val projectFunction: IrFunction,
) : IrElementTransformerVoid() {

    private val aggregateContext = aggregateClass.defaultType

    override fun visitFunction(declaration: IrFunction): IrStatement {
        val isAggregateFunction = declaration.extensionReceiverParameter?.type?.isAssignableFrom(aggregateContext)
            ?: declaration.dispatchReceiverParameter?.type?.isAssignableFrom(aggregateClass.defaultType)
            ?: false
        if (isAggregateFunction || hasAggregateInArguments(declaration)) {
            /*
             This transformation is needed to project field inside the `alignOn` function called directly by the user.
             This is made before the alignment transformation because of optimization reasons:
             if the field projection is made after the alignment step, this means that for each field call
             we made a projection, which is not necessary.
             */
            declaration.transformChildren(
                FieldTransformer(pluginContext, logger, aggregateClass, projectFunction),
                null,
            )
            /*
             This transformation is needed to add the `alignOn` function call to the aggregate functions.
             */
            declaration.transformChildren(
                AlignmentTransformer(pluginContext, logger, aggregateClass, declaration, alignedOnFunction),
                StackFunctionCall(),
            )
        }
        return super.visitFunction(declaration)
    }

    private fun hasAggregateInArguments(declaration: IrFunction): Boolean {
        return declaration.valueParameters.any { it.type.isAssignableFrom(aggregateContext) }
    }
}