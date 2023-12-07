package it.unibo.collektive.utils.branch

import it.unibo.collektive.utils.common.getLambdaType
import it.unibo.collektive.utils.common.putTypeArgument
import it.unibo.collektive.utils.common.putValueArgument
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
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
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
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
): IrContainerExpression {
    return irBlock {
        val block = branch.result
        val lastExpression = when (block) {
            is IrBlock -> block.statements.lastOrNull() as IrExpression
            else -> block
        }
        +irCall(alignedOnFunction).apply {
            // Set generics type
            putTypeArgument(getReturnType(lastExpression))
            // Set aggregate context
            putArgument(alignedOnFunction.dispatchReceiverParameter!!, aggregateContextReference)
            // Set the argument that is going to be push in the stack
            putValueArgument(
                irBoolean(conditionValue),
            )
            // Create the lambda that is going to call expression
            val lambda = buildLambdaArgument(pluginContext, block)
            putValueArgument(1, lambda)
        }
    }
}

private fun IrBuilderWithScope.buildLambdaArgument(
    pluginContext: IrPluginContext,
    element: IrExpression,
): IrFunctionExpressionImpl {
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

// private fun conditionName(condition: IrExpression): String {
//    return when (condition) {
//        is IrGetValue -> condition.symbol.owner.name.asString()
//        is IrConst<*> -> "constant"
//        is IrTypeOperatorCall -> condition.operator.name + " " + condition.typeOperand.classFqName
//        is IrCall -> condition.symbol.owner.name.asString() +
//            (condition.dispatchReceiver?.let { " " + conditionName(it) } ?: "")
//        is IrWhen -> conditionName(condition.branches[0].condition) + when {
//            condition.origin == IrStatementOrigin.ANDAND -> " & " + conditionName(condition.branches[0].result)
//            else -> " | " + conditionName(condition.branches[1].result)
//        }
//
//        else -> error(
//            "The current if condition type ${condition::class} has not been handled for the alignment yet. " +
//                "Update the compiler plugin.",
//        )
//    }
// }

private fun getReturnType(expression: IrExpression): IrType {
    return when (expression) {
        is IrTypeOperatorCall -> expression.argument.type
        else -> expression.type
    }
}
