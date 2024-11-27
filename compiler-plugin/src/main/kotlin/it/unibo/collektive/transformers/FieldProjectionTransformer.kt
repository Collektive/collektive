package it.unibo.collektive.transformers

import it.unibo.collektive.utils.common.AggregateFunctionNames.FIELD_CLASS
import it.unibo.collektive.utils.common.irStatement
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

internal class FieldProjectionTransformer(
    private val pluginContext: IrPluginContext,
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
                // Set the return type
                type = fieldExpression.type
                // Set generics type of the `alignOn` function
                putTypeArgument(0, type)
                // Set extension receiver
                extensionReceiver = dispatchReceiver
                // Set function argument
                putValueArgument(0, fieldExpression)
            }
        }
    }
}
