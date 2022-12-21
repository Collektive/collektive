package io.github.elisatronetti

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.allParameters
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name

/**
 * This transform the generated IR, creating in the function declaration a new function call,
 * which is responsible to handle the alignment.
 */
class AlignmentIrElementTransformer(
    private val pluginContext: IrPluginContext,
    private val alignOnFunction: IrFunction,
    private val aggregateLambdaBody: IrSimpleFunction,
    private val aggregateClass: IrClass
) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {
        val aggregateContextRef: IrExpression? = expression.receiverAndArgs().find {
            it.type == aggregateClass.defaultType
        }

        val aggregateContext: IrExpression = if (aggregateContextRef != null) {
            aggregateContextRef
        } else {
            // Find the aggregate context looking in all the children of expression
            val childrenAggregateRefs = collectAggregateReference(aggregateClass, expression.symbol.owner)
            if (childrenAggregateRefs.isNotEmpty()) {
                childrenAggregateRefs.first()
            } else {
                return super.visitCall(expression)
            }
        }

        return irStatement(expression) {
            buildAlignOnCall(expression, aggregateContext)
        }
    }

    private fun <T : IrElement> irStatement(expression: IrCall, body: IrSingleStatementBuilder.() -> T): T =
        IrSingleStatementBuilder(
            pluginContext,
            Scope(expression.symbol),
            expression.startOffset,
            expression.endOffset
        ).build(body)

    private fun IrSingleStatementBuilder.buildAlignOnCall(
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
            val lambda = buildLambdaArgument(expression)
            putValueArgument(1, lambda)
        }
    }

    /**
     * Create a IrFunctionExpression that transform a lambda to an expression
     * that can be used as argument for another function.
     */
    private fun IrBuilderWithScope.buildLambdaArgument(
        expression: IrCall
    ): IrFunctionExpressionImpl {
        val lambda = pluginContext.irBuiltIns.createIrBuilder(aggregateLambdaBody.symbol).buildLambda(expression)
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
    private fun IrBuilderWithScope.buildLambda(
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
}
