package it.unibo.collektive.transformers

import it.unibo.collektive.utils.common.AggregateFunctionNames.ALIGN_FUNCTION_NAME
import it.unibo.collektive.utils.common.AggregateFunctionNames.DEALIGN_FUNCTION_NAME
import it.unibo.collektive.utils.common.findAggregateReference
import it.unibo.collektive.utils.common.getAlignmentToken
import it.unibo.collektive.utils.common.irStatement
import it.unibo.collektive.utils.common.isAssignableFrom
import it.unibo.collektive.utils.common.simpleFunctionName
import it.unibo.collektive.utils.stack.StackFunctionCall
import it.unibo.collektive.visitors.collectAggregateReference
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.createTmpVariable
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.receiverAndArgs
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

/**
 * This transforms the generated IR only when an aggregate computing's function is involved:
 * for each function call and branch found, they are going to be wrapped in the alignedOn
 * function.
 */
class AlignmentTransformer(
    private val pluginContext: IrPluginContext,
    private val aggregateContextClass: IrClass,
    private val functionToAlign: IrFunction,
    private val alignRawFunction: IrFunction,
    private val dealignFunction: IrFunction,
) : IrElementTransformer<StackFunctionCall> {
    private var alignedFunctions = emptyMap<String, Int>()

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(
        expression: IrCall,
        data: StackFunctionCall,
    ): IrElement {
        val contextReference =
            expression
                .receiverAndArgs()
                .find { it.type.isAssignableFrom(aggregateContextClass.defaultType) }
                ?: collectAggregateReference(aggregateContextClass, expression.symbol.owner)

        val alignmentToken = expression.getAlignmentToken()
        // If the context is null, this means that the function is not an aggregate function
        if (contextReference == null) {
            data.push(alignmentToken)
        }
        return contextReference?.let { context ->
            // We don't want to align the alignRaw and dealign functions :)
            val functionName = expression.simpleFunctionName()
            if (functionName == ALIGN_FUNCTION_NAME || functionName == DEALIGN_FUNCTION_NAME) {
                return super.visitCall(expression, data)
            }
            // If no function, the first time the counter is 1
            val actualCounter = alignedFunctions[alignmentToken]?.let { it + 1 } ?: 1
            alignedFunctions += alignmentToken to actualCounter
            // If the expression contains a lambda, this recursion is necessary to visit the children
            expression.transformChildren(this, StackFunctionCall())
            val tokenCount =
                alignedFunctions[alignmentToken] ?: error(
                    """
                    Unable to find the count for the token $alignmentToken.
                    This is may due to a bug in collektive compiler plugin.
                    """.trimIndent(),
                )
            val alignmentTokenRepresentation = "$data$alignmentToken.$tokenCount"
            // Return the modified function body to have as a first statement the alignRaw function,
            // then the body of the function to align and finally the dealign function
            generateAlignmentCode(context, functionToAlign, expression) { irString(alignmentTokenRepresentation) }
        } ?: super.visitCall(expression, data)
    }

    override fun visitBranch(
        branch: IrBranch,
        data: StackFunctionCall,
    ): IrBranch {
        branch.generateBranchAlignmentCode(true)
        return super.visitBranch(branch, data)
    }

    override fun visitElseBranch(
        branch: IrElseBranch,
        data: StackFunctionCall,
    ): IrElseBranch {
        branch.generateBranchAlignmentCode(false)
        return super.visitElseBranch(branch, data)
    }

    private fun IrBranch.generateBranchAlignmentCode(condition: Boolean) {
        result.findAggregateReference(aggregateContextClass)?.let {
            result = generateAlignmentCode(it, functionToAlign, result) { irBoolean(condition) }
        }
    }

    private fun generateAlignmentCode(
        context: IrExpression,
        function: IrFunction,
        expressionBody: IrExpression,
        alignmentToken: IrBlockBodyBuilder.() -> IrConst,
    ): IrContainerExpression =
        irStatement(pluginContext, function, expressionBody) {
            // Call the `alignRaw` function before the body of the function to align
            irBlock {
                // Call the alignRaw function
                +irCall(alignRawFunction).apply {
                    putArgument(
                        alignRawFunction.dispatchReceiverParameter
                            ?: error("The alignRaw function has no dispatch receiver parameter"),
                        context,
                    )
                    putValueArgument(0, alignmentToken(this@irStatement))
                }
                val code = irBlock { +expressionBody }
                // Call the body of the function to align
                val variableName = "blockResult"
                val variableType = expressionBody.type
                val tmpVar = createTmpVariable(code, irType = variableType, nameHint = variableName)
                // Call the `dealign` function after the body of the function to align
                +irCall(dealignFunction).apply {
                    putArgument(
                        dealignFunction.dispatchReceiverParameter
                            ?: error("The dealign function has no dispatch receiver parameter"),
                        context,
                    )
                }
                +irGet(tmpVar)
            }
        }
    }
