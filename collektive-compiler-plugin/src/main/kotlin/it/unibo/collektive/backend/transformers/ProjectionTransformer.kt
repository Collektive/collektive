/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.backend.transformers

import it.unibo.collektive.utils.common.AggregateFunctionNames
import it.unibo.collektive.utils.logging.debug
import it.unibo.collektive.utils.logging.debugPrint
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name

/**
 * Transforms the generated IR only when an operation on a field is involved:
 * for each field operation inside the `alignedOn` function and inside the bodies of a branch,
 * the field is wrapped in the `project` function.
 */
class ProjectionTransformer(
    private val pluginContext: IrPluginContext,
    private val projectFunction: IrFunction,
    private val logger: MessageCollector,
) : IrElementTransformerVoid() {

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitCall(expression: IrCall): IrExpression {
        val symbolName = expression.symbol.owner.name
        val alignRawIdentifier = Name.identifier(AggregateFunctionNames.ALIGN_FUNCTION_NAME)
        val alignedOnIdentifier = Name.identifier(AggregateFunctionNames.ALIGNED_ON_FUNCTION_NAME)
        if (symbolName == alignRawIdentifier || symbolName == alignedOnIdentifier) {
            // alignedOn call: the fields need projection
            logger.debug("Found raw-align function call: $symbolName")
            debugPrint { "Found raw-align function call: ${expression.dumpKotlinLike()}" }
            // If the expression contains a lambda, this recursion is necessary to transform the children
            expression.transformChildren(this, null)
            return expression.transform(
                ProjectFieldOnAccessTransformer(logger, pluginContext, projectFunction),
                null,
            )
        }
        return super.visitCall(expression)
    }

    override fun visitBranch(branch: IrBranch): IrBranch = visitAnyBranch(branch)

    override fun visitElseBranch(branch: IrElseBranch): IrElseBranch = visitAnyBranch(branch)

    private inline fun <reified B : IrBranch> visitAnyBranch(branch: B): B {
        branch.result.transform(this, null)
        branch.result.transform(ProjectFieldOnAccessTransformer(logger, pluginContext, projectFunction), null)
        return branch
    }
}
