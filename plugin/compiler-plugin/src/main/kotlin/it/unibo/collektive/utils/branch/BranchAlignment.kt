package it.unibo.collektive.utils.branch

import it.unibo.collektive.utils.common.getLambdaType
import it.unibo.collektive.utils.common.putTypeArgument
import it.unibo.collektive.utils.common.putValueArgument
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.patchDeclarationParents
import org.jetbrains.kotlin.name.Name

internal fun IrSingleStatementBuilder.buildAlignedOn(
    pluginContext: IrPluginContext,
    aggregateContextReference: IrExpression,
    alignedOnFunction: IrFunction,
    branch: IrBranch,
    conditionValue: Boolean,
): IrExpression = when (branch.result) {
    is IrBlock -> buildAlignedOnBlock(
        pluginContext,
        aggregateContextReference,
        alignedOnFunction,
        branch,
        conditionValue,
    )

    else -> buildAlignedOnCall(
        pluginContext,
        aggregateContextReference,
        alignedOnFunction,
        branch,
        conditionValue,
    )
}

internal fun IrSingleStatementBuilder.buildAlignedOnBlock(
    pluginContext: IrPluginContext,
    aggregateContextReference: IrExpression,
    alignedOnFunction: IrFunction,
    branch: IrBranch,
    conditionValue: Boolean,
): IrContainerExpression {
    return irBlock {
        val block = branch.result as IrBlock
        +irCall(alignedOnFunction).apply {
            // Set generics type
            val lastExpression = block.statements.last() as IrExpression
            putTypeArgument(getReturnType(lastExpression))
            // Set aggregate context
            putArgument(alignedOnFunction.dispatchReceiverParameter!!, aggregateContextReference)
            // Set the argument that is going to be push in the stack
            putValueArgument(
                irString(createConditionArgument(branch.condition, conditionValue)),
            )
            // Create the lambda that is going to call expression
            val lambda = buildLambdaArgument(pluginContext, block)
            putValueArgument(1, lambda)
        }
    }
}

internal fun IrSingleStatementBuilder.buildAlignedOnCall(
    pluginContext: IrPluginContext,
    aggregateContextReference: IrExpression,
    alignedOnFunction: IrFunction,
    branch: IrBranch,
    conditionValue: Boolean,
): IrFunctionAccessExpression {
    return irCall(alignedOnFunction).apply {
        val statement = branch.result
        // Set generics type
        putTypeArgument(getReturnType(statement))
        // Set aggregate context
        putArgument(alignedOnFunction.dispatchReceiverParameter!!, aggregateContextReference)
        // Set the argument that is going to be push in the stack
        putValueArgument(
            irString(createConditionArgument(branch.condition, conditionValue)),
        )
        // Create the lambda that is going to call expression
        val lambda = buildLambdaArgument(pluginContext, statement)
        putValueArgument(1, lambda)
    }
}

private fun IrBuilderWithScope.buildLambdaArgument(
    pluginContext: IrPluginContext,
    element: IrExpression,
): IrFunctionExpressionImpl {
    val lambda = if (element is IrBlock) {
        // The element is a IrBlock
        buildLambda(pluginContext, element)
    } else {
        buildLambda(pluginContext, element)
    }
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
    this.origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    this.visibility = DescriptorVisibilities.LOCAL
}.apply {
    this.patchDeclarationParents(this@buildLambda.parent)
    this.returnType = getReturnType(expression)
    this.body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody {
        +irReturn(expression)
    }
}

private fun IrBuilderWithScope.buildLambda(
    pluginContext: IrPluginContext,
    expression: IrBlock,
): IrSimpleFunction = pluginContext.irFactory.buildFun {
    name = Name.special("<anonymous>")
    this.origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    this.visibility = DescriptorVisibilities.LOCAL
}.apply {
    this.patchDeclarationParents(this@buildLambda.parent)
    val lastExpression = expression.statements.removeLast() as IrExpression
    this.returnType = getReturnType(lastExpression)
    this.body = context.irBuiltIns.createIrBuilder(symbol).irBlockBody {
        for (bodyStatement in expression.statements) {
            +bodyStatement
        }
        +irReturn(lastExpression)
    }
}

private fun createConditionArgument(condition: IrExpression, conditionValue: Boolean): String =
    "branch[${conditionName(condition)}, $conditionValue]"

private fun conditionName(condition: IrExpression): String {
    return when (condition) {
        is IrGetValue -> condition.symbol.owner.name.asString()
        is IrConst<*> -> "constant"
        is IrTypeOperatorCall -> condition.operator.name + " " + condition.typeOperand.asString() // 'is' in the 'when'
        is IrCall -> condition.symbol.owner.name.asString() +
            if (condition.origin == IrStatementOrigin.EXCL && condition.dispatchReceiver != null) {
                " " + conditionName(condition.dispatchReceiver!!)
            } else {
                ""
            }

        is IrWhen -> conditionName(condition.branches[0].condition) +
            if (condition.origin == IrStatementOrigin.ANDAND) {
                " & " + conditionName(condition.branches[0].result)
            } else {
                " | " + conditionName(condition.branches[1].result)
            }

        else ->
            throw IllegalStateException(
                "The current if condition type ${condition::class} has not been handled for the alignment yet. " +
                    "Update the compiler plugin.",
            )
    }
}

private fun getReturnType(expression: IrExpression): IrType {
    return when (expression) {
        is IrTypeOperatorCall -> expression.argument.type
        else -> expression.type
    }
}
