package it.unibo.collektive.utils.branch

import it.unibo.collektive.utils.common.getLambdaType
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.Name

context(MessageCollector)
internal fun IrSingleStatementBuilder.buildAlignedOn(
    pluginContext: IrPluginContext,
    aggregateContextReference: IrExpression,
    alignedOnFunction: IrFunction,
    branch: IrBranch,
    conditionValue: Boolean,
): IrContainerExpression = irBlock {
    val block = branch.result
    val lastExpression = when (block) {
        is IrBlock -> block.statements.lastOrNull() as IrExpression
        else -> block
    }
    +irCall(alignedOnFunction).apply {
        // Set the return type
        type = getReturnType(lastExpression)
        // Set generics type of the `alignOn` function
        putTypeArgument(0, type)
        // Set aggregate context
        putArgument(alignedOnFunction.dispatchReceiverParameter!!, aggregateContextReference)
        // Set the argument that is going to be push in the stack
        putValueArgument(0, irBoolean(conditionValue))
        // Create the lambda that is going to call expression
        val lambda = buildLambdaArgument(pluginContext, block)
        putValueArgument(1, lambda)
    }
}

context(MessageCollector)
private fun IrBuilderWithScope.buildLambdaArgument(
    pluginContext: IrPluginContext,
    element: IrExpression,
): IrFunctionExpression {
    val lambda = buildLambda(pluginContext, element)
    return IrFunctionExpressionImpl(
        startOffset,
        endOffset,
        getLambdaType(pluginContext, lambda),
        lambda,
        IrStatementOrigin.LAMBDA,
    )
}

private fun IrBuilderWithScope.buildLambda(
    pluginContext: IrPluginContext,
    expression: IrExpression,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.special("<anonymous>")
    origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    visibility = DescriptorVisibilities.LOCAL
}.apply {
    patchDeclarationParents(this@buildLambda.parent)
    val statements = when (expression) {
        is IrBlock -> expression.statements
        else -> mutableListOf()
    }
    val lastExpression = (statements.removeLastOrNull() ?: expression) as IrExpression
    returnType = getReturnType(lastExpression)
    body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody {
        for (bodyStatement in statements) {
            +bodyStatement
        }
        +irReturn(lastExpression)
    }
}

private fun getReturnType(expression: IrExpression): IrType {
    return when (expression) {
        is IrTypeOperatorCall -> expression.argument.type
        else -> expression.type
    }
}
