package it.unibo.collektive

import it.unibo.collektive.utils.common.AggregateFunctionNames.FIELD_CLASS
import it.unibo.collektive.utils.common.AggregateFunctionNames.PROJECT_FUNCTION
import it.unibo.collektive.utils.common.putTypeArgument
import it.unibo.collektive.utils.logging.error
import it.unibo.collektive.utils.statement.irStatement
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class FieldProjectionVisitor(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val aggregateContextClass: IrClass,
    private val aggregateReference: IrExpression?,
) : IrElementTransformerVoid() {

    private val projectFunction by lazy {
        aggregateContextClass.functions
            .filter { it.name == Name.identifier(PROJECT_FUNCTION) }
            .firstOrNull()
    }

    override fun visitGetValue(expression: IrGetValue): IrExpression {
        if (expression.type.classFqName == FqName(FIELD_CLASS)) {
            aggregateReference?.let { aggregateRef ->
                projectFunction?.let { projectFunc ->
                    return wrapInProjectFunction(expression, aggregateRef, projectFunc)
                } ?: logger.error(
                    """
                        Failed to look up the `Field.project` function required for performing the field projection.
                        This can happen if the `AggregateContext` class is not found by the compiler plugin.
                    """.trimIndent()
                )
            } ?: logger.error(
                """
                    Fail to look up the aggregate context reference to perform the field projection.
                    This can happen if the aggregate context reference is not available in the current scope,
                    and likely means that a field operation is being performed outside of an aggregate scope.
                """.trimIndent()
            )
        }
        return super.visitGetValue(expression)
    }

    private fun wrapInProjectFunction(
        expression: IrExpression,
        context: IrExpression,
        function: IrFunction
    ): IrExpression {
        return irStatement(pluginContext, function, expression) {
            irCall(function).apply {
                // Set generics type
                putTypeArgument(expression.type)
                // Set aggregate context
                putArgument(function.dispatchReceiverParameter!!, context)
                putValueArgument(0, expression)
            }
        }
    }
}