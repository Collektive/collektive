package io.github.elisatronetti.utils.branch

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
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.Name

fun IrSingleStatementBuilder.buildAlignOnBlock(
    pluginContext: IrPluginContext,
    alignOnFunction: IrFunction,
    branch: IrBranch,
    conditionValue: Boolean,
    aggregateContext: IrExpression
): IrContainerExpression {
    return irBlock {
        val block = branch.result as IrBlock
        +irCall(alignOnFunction).apply {
            // Set generics type
            val lastExpression = block.statements.last() as IrExpression
            val returnType = getReturnType(lastExpression)
            this.type = returnType
            putTypeArgument(0, returnType)

            // Set aggregate context
            putArgument(alignOnFunction.dispatchReceiverParameter!!, aggregateContext)
            // Set the argument that is going to be push in the stack
            putValueArgument(
                0,
                irString(createConditionArgument(branch.condition, conditionValue))
            )
            // Create the lambda that is going to call expression
            val lambda = buildLambdaArgument(pluginContext, block)
            putValueArgument(1, lambda)
        }
    }
}

fun IrSingleStatementBuilder.buildAlignOnCall(
    pluginContext: IrPluginContext,
    alignOnFunction: IrFunction,
    branch: IrBranch,
    conditionValue: Boolean,
    aggregateContext: IrExpression
): IrFunctionAccessExpression {
    return irCall(alignOnFunction).apply {
        val statement = branch.result
        // Set generics type
        val returnType = getReturnType(statement)
        this.type = returnType
        putTypeArgument(0, returnType)

        // Set aggregate context
        putArgument(alignOnFunction.dispatchReceiverParameter!!, aggregateContext)
        // Set the argument that is going to be push in the stack
        putValueArgument(
            0,
            irString(createConditionArgument(branch.condition, conditionValue))
        )
        // Create the lambda that is going to call expression
        val lambda = buildLambdaArgument(pluginContext, statement)
        putValueArgument(1, lambda)
    }
}

fun IrBuilderWithScope.buildLambdaArgument(
    pluginContext: IrPluginContext,
    statement: IrExpression
): IrFunctionExpressionImpl {
    val lambda = buildLambda(pluginContext, statement)
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
    block: IrBlock
): IrFunctionExpressionImpl {
    val lambda = buildLambda(pluginContext, block)
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

fun IrBuilderWithScope.buildLambda(
    pluginContext: IrPluginContext,
    expression: IrExpression
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.special("<anonymous>")
    this.origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    this.visibility = DescriptorVisibilities.LOCAL
}.apply {
    this.patchDeclarationParents(this@buildLambda.parent)
    this.returnType = getReturnType(expression)
    this.body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody {
        +irReturn(expression)
    }
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
    this.returnType = getReturnType(lastExpression)
    this.body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody {
        for (bodyStatement in expression.statements) { +bodyStatement }
        +irReturn(lastExpression)
    }
}

private fun createConditionArgument(condition: IrExpression, conditionValue: Boolean): String {
    when (condition) {
        is IrGetValue -> return Pair(condition.symbol.owner.name.asString(), conditionValue.toString()).toString()
        is IrConst<*> -> return Pair("constant", conditionValue.toString()).toString()
        is IrCall -> return Pair(condition.symbol.owner.name.asString(), conditionValue.toString()).toString()
    }
    throw IllegalStateException(
        "The current if condition type ${condition::class} has not been handled for the alignment yet. " +
                "Update the compiler plugin."
    )
}

private fun getReturnType(expression: IrExpression): IrType {
    when (expression) {
        is IrTypeOperatorCall -> return expression.argument.type
        is IrCall -> return expression.type
        is IrConst<*> -> return expression.type
        is IrGetObjectValue -> return expression.type
        is IrConstructorCall -> return expression.type
        is IrGetValue -> return expression.type
        is IrFunctionExpression -> return expression.type
    }
    throw IllegalStateException(
        "The current if condition body ${expression::class} has not been handled for the alignment yet. " +
                "Update the compiler plugin."
    )
}
