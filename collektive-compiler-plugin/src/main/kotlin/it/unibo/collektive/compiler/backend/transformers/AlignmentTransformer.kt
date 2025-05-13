/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.backend.transformers

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.compiler.backend.utils.StackFunctionCall
import it.unibo.collektive.compiler.utils.common.findAggregateReference
import it.unibo.collektive.compiler.utils.common.irStatement
import it.unibo.collektive.compiler.utils.common.simpleFunctionName
import it.unibo.collektive.compiler.utils.common.toFunctionAlignmentToken
import it.unibo.collektive.compiler.utils.logging.debugPrint
import it.unibo.collektive.compiler.utils.logging.error
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.createTmpVariable
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * IR transformer that injects alignment logic around aggregate-aware computations.
 *
 * This transformer traverses the Intermediate Representation (IR) of functions
 * and wraps:
 * - Function calls
 * - Conditional branches
 *
 * in calls to `alignRaw` and `dealign`, ensuring that computations involving
 * aggregate context are properly aligned across devices.
 *
 * ## Transformation Strategy:
 * - Function calls that operate in an aggregate context are intercepted.
 * - A call to `alignRaw(context, alignmentToken)` is injected before the computation.
 * - The computation result is captured.
 * - A call to `dealign(context)` is injected after the computation.
 * - The original result is returned.
 *
 * ## Alignment Tokens:
 * Each aligned computation is associated with a unique token string
 * that encodes the call's semantics (function name, type arguments, arguments).
 *
 * If the same call is encountered multiple times within the same scope,
 * the token is annotated with a count (`N|`) to distinguish instances.
 *
 * ## Special Cases:
 * - Functions explicitly annotated with [it.unibo.collektive.aggregate.api.CollektiveIgnore]
 *   are **excluded** from alignment.
 * - Internal operations like accessing a `Field.context`, `alignRaw`, and `dealign` themselves are also **excluded**.
 *
 * @property pluginContext the plugin context used for constructing IR nodes
 * @property aggregateClass the IR class symbol for `Aggregate<ID>`
 * @property fieldClass the IR class symbol for `Field<ID, *>`
 * @property functionToAlign the IR function being processed
 * @property alignRawFunction the IR function representing `Aggregate.alignRaw`
 * @property dealignFunction the IR function representing `Aggregate.dealign`
 * @property getContext the IR function representing `Field.context`
 * @property logger a [MessageCollector] used for debug and error reporting
 */
class AlignmentTransformer(
    private val pluginContext: IrPluginContext,
    private val aggregateClass: IrClass,
    private val fieldClass: IrClass,
    private val functionToAlign: IrFunction,
    private val alignRawFunction: IrFunction,
    private val dealignFunction: IrFunction,
    private val getContext: IrFunction,
    private val logger: MessageCollector,
) : IrTransformer<StackFunctionCall>() {

    /**
     * A mapping from alignment tokens to the number of times they have been encountered.
     *
     * Used to disambiguate multiple identical calls.
     */
    private val alignedFunctions = mutableMapOf<String, Int>()

    @OptIn(ExperimentalAtomicApi::class)
    private val counter = AtomicInt(0)

    private fun IrElement.alignmentToken(): String {
        val token = when (this) {
            is IrFunctionAccessExpression -> toFunctionAlignmentToken()
            else -> dumpKotlinLike()
        }
        val occurrence = alignedFunctions.getOrPut(token) { 0 } + 1
        alignedFunctions[token] = occurrence
        val nth = if (occurrence > 1) "($occurrence)" else ""
        return "$nth$token"
    }

    private fun visitNotAlignable(expression: IrDeclarationReference, data: StackFunctionCall): IrExpression {
        val token = expression.alignmentToken()
        data.push(token)
        return super.visitDeclarationReference(expression, data)
    }

    /**
     * Visits regular functions and logs their discovery.
     */
    override fun visitFunction(declaration: IrFunction, data: StackFunctionCall): IrStatement {
        debugPrint {
            "Encountered: ${if (declaration.isInline) "inline" else "non-inline"} ${declaration.dumpKotlinLike()}"
        }
        return super.visitFunction(declaration, data)
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun visitDeclarationReference(expression: IrDeclarationReference, data: StackFunctionCall): IrExpression {
        val aggregateContext = expression.findAggregateReference()
        return when (aggregateContext) {
            null -> visitNotAlignable(expression, data)
            else -> {
                val token = "$data${expression.alignmentToken()}"
                generateAlignmentCode(aggregateContext, functionToAlign, expression) {
                    irString(token)
                }
            }
        }
    }

    /**
     * Looks up the [Aggregate] execution context captured by this [IrFunctionAccessExpression].
     *
     * This is a shortcut that automatically uses the [AlignmentTransformer]'s
     * configured [pluginContext], [aggregateClass], [fieldClass], [getContext], and [logger].
     *
     * @receiver the function access expression to inspect
     * @return an [IrExpression] referencing the aggregate context, or `null` if none found
     */
    private fun IrExpression.findAggregateReference() =
        findAggregateReference(pluginContext, aggregateClass, fieldClass, getContext, logger)

    /**
     * Detects whether this function access is **not** a candidate for alignment.
     *
     * Excludes calls to:
     * - [alignRawFunction]
     * - [dealignFunction], or
     * - [it.unibo.collektive.aggregate.Field.context]
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrFunctionAccessExpression.isNotAnAlignmentTarget(): Boolean = symbol.owner.let { owner ->
        owner == getContext || owner == alignRawFunction || owner == dealignFunction
    }

    /**
     * Visits function calls and injects alignment logic when necessary.
     *
     * If a call operates in an aggregate context:
     * - Wraps it with `alignRaw` and `dealign`
     * - Tracks alignment tokens
     * - Recurses into lambda arguments
     *
     * Otherwise, simply visits children recursively.
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class, ExperimentalAtomicApi::class)
    override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: StackFunctionCall): IrElement {
        if (expression.isNotAnAlignmentTarget()) {
            return visitNotAlignable(expression, data)
        }
        val context = expression.findAggregateReference()
        return when {
            context == null -> visitNotAlignable(expression, data)
            else -> {
                val alignmentToken = expression.alignmentToken()
                // Check the provenance of the context:
                val contextRefVar = (context as? IrGetValue)?.symbol?.owner
                if (contextRefVar != null) {
                    val isTempVar = contextRefVar.origin == IrDeclarationOrigin.IR_TEMPORARY_VARIABLE
                    if (isTempVar && contextRefVar.name.asString().startsWith(CONTEXT_VARIABLE_NAME)) {
                        logger.error(
                            "Double alignment detected in ${
                                expression.simpleFunctionName()
                            }:\n${
                                expression.dumpKotlinLike()
                            }",
                        )
                    }
                }
                // If the expression contains a lambda, this recursion is necessary to visit the children
                expression.transformChildren(this, StackFunctionCall())
                val fullToken = "$data$alignmentToken"
                // Return the modified function body to have as a first statement the alignRaw function,
                // then the body of the function to align and finally the dealign function
                generateAlignmentCode(context, expression.symbol.owner, expression) { irString(fullToken) }
            }
        }
    }

    /**
     * Visits a conditional branch (`if` or `when` case) and injects alignment
     * if its result is aggregate-aware.
     */
    override fun visitBranch(branch: IrBranch, data: StackFunctionCall): IrBranch {
        branch.generateBranchAlignmentCode(true)
        return super.visitBranch(branch, data)
    }

    /**
     * Visits an `else` branch and injects alignment if needed.
     */
    override fun visitElseBranch(branch: IrElseBranch, data: StackFunctionCall): IrElseBranch {
        branch.generateBranchAlignmentCode(false)
        return super.visitElseBranch(branch, data)
    }

    private fun IrBranch.generateBranchAlignmentCode(condition: Boolean) {
        result.findAggregateReference(pluginContext, aggregateClass, fieldClass, getContext, logger)?.let {
            result = generateAlignmentCode(it, functionToAlign, result) { irBoolean(condition) }
        }
    }

    /**
     * Generates an IR block that wraps a computation with:
     * - `align` before
     * - a Computation result captured
     * - `dealign` after
     *
     * This block guarantees alignment consistency for the duration of the computation.
     *
     * @param context the IR expression providing the aggregate context
     * @param function the enclosing IR function
     * @param expressionBody the computation to wrap
     * @param alignmentToken a builder producing the token expression
     * @return a new [IrContainerExpression] representing the aligned computation
     */
    @OptIn(ExperimentalAtomicApi::class)
    private fun generateAlignmentCode(
        context: IrExpression,
        function: IrFunction,
        expressionBody: IrExpression,
        alignmentToken: IrBlockBodyBuilder.() -> IrConst,
    ): IrContainerExpression = irStatement(pluginContext, function, expressionBody) {
        // Call the `alignRaw` function before the body of the function to align
        val generationId = counter.fetchAndAdd(1)
        irBlock {
            // ✅ Create a declared, scoped, bound variable from the context
            val contextAccess = createTmpVariable(
                context,
                irType = context.type,
                nameHint = "$CONTEXT_VARIABLE_NAME$generationId",
            )
            // Call the align function
            +irCall(alignRawFunction).apply {
                putArgument(
                    alignRawFunction.dispatchReceiverParameter
                        ?: error("The alignRaw function has no dispatch receiver parameter"),
                    irGet(contextAccess),
                )
                putValueArgument(0, alignmentToken(this@irStatement))
            }
            val blockResult = createTmpVariable(
                expressionBody,
                irType = expressionBody.type,
                nameHint = "$ALIGNED_COMPUTATION_VARIABLE_NAME$generationId",
            )
            // Call the `dealign` function after the body of the function to align
            +irCall(dealignFunction).apply {
                putArgument(
                    dealignFunction.dispatchReceiverParameter
                        ?: error("The dealign function has no dispatch receiver parameter"),
                    irGet(contextAccess),
                )
            }
            // ✅ Return block result
            +irGet(blockResult)
        }
    }

    private companion object {
        const val CONTEXT_VARIABLE_NAME = "collektiveAggregateContext"
        const val ALIGNED_COMPUTATION_VARIABLE_NAME = "collektiveAlignedComputationResult"
    }
}
