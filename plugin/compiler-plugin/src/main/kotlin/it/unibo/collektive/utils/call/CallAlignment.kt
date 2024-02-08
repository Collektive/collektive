package it.unibo.collektive.utils.call

import it.unibo.collektive.utils.common.getAlignmentToken
import it.unibo.collektive.utils.common.getLambdaType
import it.unibo.collektive.utils.common.putTypeArgument
import it.unibo.collektive.utils.common.putValueArgument
import it.unibo.collektive.utils.stack.StackFunctionCall
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.Name

context(MessageCollector)
fun IrSingleStatementBuilder.buildAlignedOnCall(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrFunction,
    aggregateContextReference: IrExpression,
    alignedOnFunction: IrFunction,
    expression: IrCall,
    stack: StackFunctionCall,
    data: Map<String, Int>,
): IrFunctionAccessExpression {
    return irCall(alignedOnFunction).apply {
        // Set generics type
        putTypeArgument(expression.type)
        // Set aggregate context
        putArgument(alignedOnFunction.dispatchReceiverParameter!!, aggregateContextReference)
        // Set the argument that is going to be push in the stack
        val functionName = expression.getAlignmentToken()
        val count = data[functionName]!! // Here the key should be present!
        val alignmentToken = stack.toString() + functionName + count
        putValueArgument(
            irString(alignmentToken),
        )
        // Create the lambda that is going to call expression
        val lambda = buildLambdaArgument(pluginContext, aggregateLambdaBody, expression)
        putValueArgument(1, lambda)
    }
}

/**
 * Create a IrFunctionExpression that transform a lambda to an expression
 * that can be used as argument for another function.
 */
context(MessageCollector)
private fun IrBuilderWithScope.buildLambdaArgument(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrFunction,
    expression: IrCall,
): IrFunctionExpressionImpl {
    val lambda = pluginContext
        .irBuiltIns
        .createIrBuilder(aggregateLambdaBody.symbol)
        .buildLambda(pluginContext, expression)
    return IrFunctionExpressionImpl(
        startOffset,
        endOffset,
        getLambdaType(pluginContext, lambda),
        lambda,
        IrStatementOrigin.LAMBDA,
    )
}

/**
 * Build a new lambda, which parent is the IrElement where the function is called.
 * The body of the lambda calls the expression passed as argument. If expression
 * has a return type, the lambda body will have the same return type.
 */
private fun IrBuilderWithScope.buildLambda(
    pluginContext: IrPluginContext,
    expression: IrCall,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.special("<anonymous>")
    returnType = expression.type
    origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    visibility = DescriptorVisibilities.LOCAL
}.apply {
    patchDeclarationParents(this@buildLambda.parent)
    body = when (expression.symbol.owner.returnType.isUnit()) {
        true -> context.irBuiltIns.createIrBuilder(symbol).irBlockBody { +expression }
        false -> context.irBuiltIns.createIrBuilder(symbol).irBlockBody { +irReturn(expression) }
    }
}
