/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.backend.transformers

import it.unibo.collektive.utils.common.isAggregate
import it.unibo.collektive.utils.common.isConcrete
import it.unibo.collektive.utils.logging.debug
import it.unibo.collektive.utils.logging.debugPrint
import it.unibo.collektive.utils.stack.StackFunctionCall
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * IR transformer that identifies aggregate function definitions
 * and applies alignment transformations to their bodies.
 *
 * ## Purpose:
 * - Detects functions that operate over [Aggregate]s or [Field]s.
 * - Projects field values inside user-written branches and `alignOn` blocks.
 * - Injects `alignRaw` and `dealign` calls around aggregate computations
 *   using the [AlignmentTransformer].
 *
 * ## Transformation Phases:
 * 1. **Projection Phase**:
 *    Before aligning, the [ProjectionTransformer] is applied to pre-process
 *    fields inside branches and explicit `alignOn` calls.
 * 2. **Alignment Phase**:
 *    After projection, the [AlignmentTransformer] wraps computations
 *    involving aggregates with appropriate alignment operations.
 *
 * ## Conditions:
 * A function is transformed if:
 * - It is **concrete** (not abstract), and
 * - It is **aggregate-aware** (determined by parameter or receiver types).
 *
 * @property pluginContext the plugin context used for IR construction
 * @property logger a [MessageCollector] used for debug and error reporting
 * @property aggregateClass the IR class for `Aggregate<ID>`
 * @property fieldClass the IR class for `Field<ID, *>`
 * @property alignRawFunction the IR symbol for the `alignRaw` operation
 * @property dealignFunction the IR symbol for the `dealign` operation
 * @property projectFunction the IR symbol for the `project` function
 * @property getContext the IR symbol for the `Field.context` getter
 */
class AggregateFunctionTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: MessageCollector,
    private val aggregateClass: IrClass,
    private val fieldClass: IrClass,
    private val alignRawFunction: IrFunction,
    private val dealignFunction: IrFunction,
    private val projectFunction: IrFunction,
    private val getContext: IrFunction,
) : IrElementTransformerVoid() {

    /**
     * Visits IR functions and applies projection and alignment transformations
     * if the function is concrete and aggregate-aware.
     *
     * @param declaration the [IrFunction] to visit
     * @return the (potentially transformed) [IrFunction]
     */
    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitFunction(declaration: IrFunction): IrStatement {
        if (declaration.isConcrete && declaration.isAggregate(aggregateClass, fieldClass, logger)) {
            logger.debug("Found aggregate function: ${declaration.symbol.owner.name}")
            debugPrint { "Aggregate function ${declaration.symbol.owner.name}" }
            val preProjection = debugPrint { declaration.dumpKotlinLike() }
            debugPrint { "Pre-alignment function: \n $preProjection" }
            /*
             * This transformation is needed to project fields inside branches and user-written `alignOn` operations.
             * Projection is performed before alignment to avoid unnecessary projections after every
             * plugin-inserted alignment operation.
             */
            declaration.transformChildren(
                ProjectionTransformer(pluginContext, projectFunction, logger),
                null,
            )
            val postProjection = debugPrint { declaration.dumpKotlinLike() }
            if (preProjection == postProjection) {
                debugPrint { "No projection needed" }
            } else {
                debugPrint { "Projected: $postProjection" }
            }
            /*
             This transformation is needed to add the `alignRaw` and `dealign` function call to the aggregate functions.
             */
            declaration.transformChildren(
                AlignmentTransformer(
                    pluginContext,
                    aggregateClass,
                    fieldClass,
                    declaration,
                    alignRawFunction,
                    dealignFunction,
                    getContext,
                    logger,
                ),
                StackFunctionCall(),
            )
            debugPrint { "Aligned function:\n${declaration.dumpKotlinLike()}" }
            debugPrint { "-------------" }
            return declaration
        } else {
            return super.visitFunction(declaration)
        }
    }
}
