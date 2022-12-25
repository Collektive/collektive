package io.github.elisatronetti.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.allParameters
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.Name

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

fun IrBuilderWithScope.buildLambdaArgument(
    pluginContext: IrPluginContext,
    aggregateLambdaBody: IrSimpleFunction,
    block: IrBlock
): IrFunctionExpressionImpl {
    val lambda = buildLambda(pluginContext, block)
    val base = if (lambda.isSuspend)
        pluginContext.referenceClass(
            StandardNames.getSuspendFunctionClassId(lambda.allParameters.size).asSingleFqName()
        )
            ?: error("suspend function type not found")
    else
        pluginContext.referenceClass(StandardNames.getFunctionClassId(lambda.allParameters.size).asSingleFqName())
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

fun IrBuilderWithScope.buildLambda(
    pluginContext: IrPluginContext,
    expression: IrBlock
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.special("<anonymous>")
    this.origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    this.visibility = DescriptorVisibilities.LOCAL
}.apply {
    this.patchDeclarationParents(this@buildLambda.parent)
    val lastExpression = expression.statements.removeLast() as IrExpression
    if (lastExpression is IrTypeOperatorCall){
        this.returnType = lastExpression.argument.type
    }
    this.body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody {
        for (bodyStatement in expression.statements) { +bodyStatement }
        +irReturn(lastExpression)
    }
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