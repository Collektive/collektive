package io.github.elisatronetti

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.allParameters
import org.jetbrains.kotlin.backend.common.ir.setDeclarationsParent
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.jvm.ir.defaultValue
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.impl.FunctionExpressionDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.backend.js.utils.invokeFunForLambda
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.IrFunctionBuilder
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrSyntheticBodyImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isUnit
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.model.FunctionExpression
import org.jetbrains.kotlin.utils.addToStdlib.cast

/**
 * This transform the generated IR, creating in the function declaration a new function call,
 * which is responsible to handle the alignment.
 */
class AlignmentIrElementTransformer(
    private val pluginContext: IrPluginContext,
    private val alignOnFunction: IrFunction,
    private val aggregateCall: IrCall,
    private val aggregateContext: IrClass
) : IrElementTransformerVoid() {

    override fun visitCall(expression: IrCall): IrExpression {
        val callee: IrFunction = expression.symbol.owner // Declaration of the function call
        val aggregateContextRef: IrExpression? = expression.receiverAndArgs().find {
            it.type == aggregateContext.defaultType
        }
        val aggregateLambda = (aggregateCall.getValueArgument(0) as IrFunctionExpression).function
        fun <T : IrElement> irStatement(body: IrSingleStatementBuilder.() -> T): T =
            IrSingleStatementBuilder(
                pluginContext,
                Scope(expression.symbol),
                expression.startOffset,
                expression.endOffset
            ).build(body)
        if (aggregateContextRef != null) {
            val a = irStatement {
                irCall(alignOnFunction).apply {
                    val x = pluginContext.irBuiltIns.createIrBuilder(aggregateLambda.symbol).buildLambda(expression)
                    type = expression.type
                    putTypeArgument(0, expression.type)
                    putArgument(alignOnFunction.dispatchReceiverParameter!!, aggregateContextRef)
                    putValueArgument(
                        0,
                        irString(callee.symbol.owner.kotlinFqName.asString())
                    )
                    val base = pluginContext.referenceClass(
                        StandardNames.getFunctionClassId(x.allParameters.size).asSingleFqName()
                    )
                        ?: error("function type not found")

                    val type: IrType = base.typeWith(x.allParameters.map { it.type } + x.returnType)
                    val y = IrFunctionExpressionImpl(
                        startOffset,
                        endOffset,
                        type,
                        x,
                        IrStatementOrigin.LAMBDA
                    )
                    putValueArgument(1, y)
                }
            }
            return a
        } else {
                val aggregateContextForse = collectAggregateReference(aggregateContext, expression.symbol.owner)
                if (aggregateContextForse.isNotEmpty()) {
                    val a = irStatement {
                        irCall(alignOnFunction).apply {
                            val x = pluginContext.irBuiltIns.createIrBuilder(aggregateLambda.symbol).buildLambda(expression)
                            type = expression.type
                            putTypeArgument(0, expression.type)
                            putArgument(alignOnFunction.dispatchReceiverParameter!!, aggregateContextForse.first())
                            putValueArgument(
                                0,
                                irString(callee.symbol.owner.kotlinFqName.asString())
                            )
                            val base = pluginContext.referenceClass(
                                StandardNames.getFunctionClassId(x.allParameters.size).asSingleFqName()
                            )
                                ?: error("function type not found")

                            val type: IrType = base.typeWith(x.allParameters.map { it.type } + x.returnType)
                            val y = IrFunctionExpressionImpl(
                                startOffset,
                                endOffset,
                                type,
                                x,
                                IrStatementOrigin.LAMBDA
                            )
                            putValueArgument(1, y)
                        }
                    }
                    return a
                }
            }
        return super.visitCall(expression)
    }

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
