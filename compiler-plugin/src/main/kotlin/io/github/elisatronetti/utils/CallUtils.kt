package io.github.elisatronetti.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.allParameters
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.Name

fun IrSingleStatementBuilder.buildAlignOnCall(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrSimpleFunction,
    alignOnFunction: IrFunction,
    expression: IrCall,
    aggregateContext: IrExpression
): IrFunctionAccessExpression {
    return irCall(alignOnFunction).apply {
        // Set generics type
        type = expression.type
        putTypeArgument(0, expression.type)
        // Set aggregate context
        putArgument(alignOnFunction.dispatchReceiverParameter!!, aggregateContext)
        // Set the argument that is going to be push in the stack
        putValueArgument(
            0,
            irString(expression.symbol.owner.kotlinFqName.asString())
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
fun IrBuilderWithScope.buildLambdaArgument(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrSimpleFunction,
    expression: IrCall
): IrFunctionExpressionImpl {
    val lambda = pluginContext.irBuiltIns.createIrBuilder(aggregateLambdaBody.symbol).buildLambda(pluginContext, expression)
    val base = pluginContext.referenceClass(
        StandardNames.getFunctionClassId(lambda.allParameters.size).asSingleFqName()
    )
        ?: error("function type not found")
    val type: IrType = base.typeWith(lambda.allParameters.map { it.type } + lambda.returnType)
    return IrFunctionExpressionImpl(
        startOffset,
        endOffset,
        type,
        lambda,
        IrStatementOrigin.LAMBDA
    )
}

/**
 * Build a new lambda, which parent is the IrElement where the function is called.
 * The body of the lambda calls the expression passed as argument. If expression
 * has a return type, the lambda body will have the same return type.
 */
fun IrBuilderWithScope.buildLambda(
    pluginContext: IrPluginContext,
    expression: IrCall
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
