/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.backend.irextensions

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.compiler.backend.util.debugPrint
import it.unibo.collektive.compiler.common.debug
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid

/**
 * Attempts to locate a reference to the [Aggregate] execution context
 * within this [IrExpression].
 *
 * This function looks for captured variables of types:
 * - [Aggregate], used directly if found
 * - [Field], from which the context is derived by calling the `context` getter
 *
 * If a [Field] is found instead of an [Aggregate], a synthetic IR call
 * is injected to retrieve the associated [Aggregate].
 *
 * @param pluginContext the IR plugin context for constructing synthetic nodes
 * @param aggregateClass the IR class representing the `Aggregate<ID>` interface
 * @param fieldClass the IR class representing the `Field<ID, *>` interface
 * @param getContext the IR function symbol representing the `context` getter
 * @param logger an optional [MessageCollector] for debug output
 * @return an [IrExpression] accessing the [Aggregate] context, or `null` if none found
 */
internal fun IrExpression.findAggregateReference(
    pluginContext: IrPluginContext,
    aggregateClass: IrClass,
    fieldClass: IrClass,
    getContext: IrFunction,
    logger: MessageCollector?,
): IrExpression? = findFirstCapturedVariableOfType(aggregateClass)
    ?: findFirstCapturedVariableOfType(fieldClass)
        ?.also {
            logger?.debug("Found aggregate context in $this: $it")
            debugPrint { "Found aggregate context in ${this.dumpKotlinLike()}: ${it.dumpKotlinLike()}" }
        }
        ?.buildGetFieldContext(pluginContext, aggregateClass, fieldClass, getContext)
        ?.also {
            logger?.debug("Found field reference in $this: $it")
            debugPrint { "Field-mediated context in ${this.dumpKotlinLike()}: ${it.dumpKotlinLike()}" }
        }

/**
 * Traverses this [IrExpression] subtree and finds the first [IrGetValue]
 * whose type matches the provided [targetType].
 *
 * Performs a depth-first search to locate the matching captured variable.
 *
 * @param targetType the target IR class type to match
 * @return the first matching [IrGetValue], or `null` if no match is found
 */
internal fun IrExpression.findFirstCapturedVariableOfType(targetType: IrClass): IrGetValue? {
    var result: IrGetValue? = null
    accept(
        object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) {
                if (result == null) {
                    element.acceptChildren(this, null)
                }
            }

            override fun visitGetValue(expression: IrGetValue) {
                if (result == null) {
                    if (expression.type.isAssignableFrom(targetType.defaultType)) {
                        result = expression
                    }
                }
            }
        },
        null,
    )
    return result
}
