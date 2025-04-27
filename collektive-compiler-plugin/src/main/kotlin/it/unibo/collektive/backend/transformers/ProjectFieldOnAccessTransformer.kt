/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.backend.transformers

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.utils.common.AggregateFunctionNames.FIELD_CLASS
import it.unibo.collektive.utils.common.isAnnotatedAsPurelyLocal
import it.unibo.collektive.utils.logging.debug
import it.unibo.collektive.utils.logging.debugPrint
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.putArgument
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

/**
 * IR transformer that wraps accesses to [Field] values inside a `project` function call.
 *
 * ## Purpose:
 * When a field is accessed directly (i.e., its [IrGetValue] is used),
 * this transformer replaces the raw access with a call to the `project` function.
 *
 * This ensures that field values are safely projected into local computations,
 * avoiding the need for projections after plugin-inserted alignment transformations.
 *
 * ## How It Works:
 * - Detects [IrGetValue] expressions whose type matches [Field].
 * - Replaces them with a call to the provided [projectFunction].
 * - The resulting IR call has the same type as the original field access.
 *
 * ## Notes:
 * This transformation is applied **before** the alignment phase,
 * ensuring that user-written branches and alignments work seamlessly.
 *
 * @property logger a [MessageCollector] used for debug logging
 * @property pluginContext the plugin context used to construct new IR nodes
 * @property projectFunction the IR symbol for the `project` helper function
 */
internal class ProjectFieldOnAccessTransformer(
    private val logger: MessageCollector,
    private val pluginContext: IrPluginContext,
    private val projectFunction: IrFunction,
) : IrElementTransformerVoid() {

    override fun visitFunction(declaration: IrFunction): IrStatement = when {
        declaration.isAnnotatedAsPurelyLocal() -> declaration
        else -> super.visitFunction(declaration)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFunctionAccess(expression: IrFunctionAccessExpression): IrExpression = when {
        expression.symbol.owner.isAnnotatedAsPurelyLocal() -> expression
        else -> super.visitFunctionAccess(expression)
    }

    /**
     * Visits [IrGetValue] expressions and wraps [Field] accesses
     * inside a call to the `project` function.
     *
     * @param expression the [IrGetValue] IR node
     * @return the (potentially wrapped) [IrExpression]
     */
    override fun visitGetValue(expression: IrGetValue): IrExpression {
        if (expression.type.classFqName == FqName(FIELD_CLASS)) {
            logger.debug("This expression returns a field: $expression")
            debugPrint { "This expression returns a field: ${expression.dumpKotlinLike()}" }
            return wrapInProjectFunction(expression) // , aggregateReference)
        }
        return super.visitGetValue(expression)
    }

    /**
     * Wraps a [Field] access into a call to the `project` function.
     *
     * @param fieldExpression the IR expression representing the field
     * @return a new IR call expression to `project(field)`
     */
    private fun wrapInProjectFunction(fieldExpression: IrGetValue): IrExpression = IrSingleStatementBuilder(
        pluginContext,
        Scope(fieldExpression.symbol),
        fieldExpression.startOffset,
        fieldExpression.endOffset,
    )
        .irCall(projectFunction).apply {
            debugPrint { "Projecting: ${fieldExpression.dumpKotlinLike()}" }
            this.type = fieldExpression.type
            val simpleType = fieldExpression.type as? IrSimpleType
            val typeArguments = simpleType?.arguments
                ?.mapNotNull { it.typeOrNull }
                .orEmpty()
            if (typeArguments.size == 2) {
                putTypeArgument(0, typeArguments[0])
                putTypeArgument(1, typeArguments[1])
            }
            putArgument(projectFunction.valueParameters.single(), fieldExpression)
        }
}
