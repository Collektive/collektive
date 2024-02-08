package it.unibo.collektive.transformers

import it.unibo.collektive.utils.common.AggregateFunctionNames.FIELD_CLASS
import it.unibo.collektive.utils.common.putTypeArgument
import it.unibo.collektive.utils.statement.irStatement
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

class FieldProjectionTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val projectFunction: IrFunction,
    private val aggregateReference: IrExpression,
) : IrElementTransformerVoid() {

    override fun visitGetValue(expression: IrGetValue): IrExpression {
        if (expression.type.classFqName == FqName(FIELD_CLASS)) {
            return wrapInProjectFunction(expression, projectFunction, aggregateReference)
        }
        return super.visitGetValue(expression)
    }

    private fun wrapInProjectFunction(
        fieldExpression: IrExpression,
        projectFunction: IrFunction,
        dispatchReceiver: IrExpression,
    ): IrExpression {
        return irStatement(pluginContext, projectFunction, fieldExpression) {
            irCall(projectFunction).apply {
                // Set generics type
                putTypeArgument(fieldExpression.type)
                // Set extension receiver
                extensionReceiver = dispatchReceiver
                // Set function argument
                putValueArgument(0, fieldExpression)
            }
        }
    }
}
