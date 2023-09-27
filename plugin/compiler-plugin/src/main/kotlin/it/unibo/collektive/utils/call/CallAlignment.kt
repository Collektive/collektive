package it.unibo.collektive.utils.call

import it.unibo.collektive.AlignedData
import it.unibo.collektive.utils.common.getLambdaType
import it.unibo.collektive.utils.common.putTypeArgument
import it.unibo.collektive.utils.common.putValueArgument
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
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

fun IrSingleStatementBuilder.buildAlignedOnCall(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrFunction,
    aggregateContextReference: IrExpression,
    alignedOnFunction: IrFunction,
    expression: IrCall,
    data: AlignedData,
): IrFunctionAccessExpression {
    return irCall(alignedOnFunction).apply {
        // Set generics type
        putTypeArgument(expression.type)
        // Set aggregate context
        putArgument(alignedOnFunction.dispatchReceiverParameter!!, aggregateContextReference)
        // Set the argument that is going to be push in the stack
        val functionName = expression.symbol.owner.name.asString()
        val count = data[functionName]!! // Here the key should be present!
        putValueArgument(
            irString("$functionName.$count"),
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
    this.returnType = expression.type
    this.origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    this.visibility = DescriptorVisibilities.LOCAL
}.apply {
    this.patchDeclarationParents(this@buildLambda.parent)
    if (expression.symbol.owner.returnType.isUnit()) {
        this.body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody { +expression }
    } else {
        this.body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody { +irReturn(expression) }
    }
}
