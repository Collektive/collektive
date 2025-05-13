/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.backend.irextensions

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
 * Attempts to locate a reference to the [it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_NAME]
 * execution context within this [IrExpression].
 *
 * This function searches for captured variables of types:
 * - [it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_NAME]: returned directly if found
 * - [it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME]:
 *   used to derive the context via the `context` getter
 *
 * If a [it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME] is found,
 * a synthetic IR call is injected to retrieve
 * the associated [it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_NAME] context.
 *
 * @param pluginContext the IR plugin context for constructing synthetic nodes
 * @param aggregateClass the IR class representing the `Aggregate<ID>` interface
 * @param fieldClass the IR class representing the
 *   [it.unibo.collektive.compiler.common.CollektiveNames.FIELD_CLASS_FQ_NAME] interface
 * @param getContext the IR function symbol representing the `context` getter
 * @param logger an optional [MessageCollector] for debug output
 * @return an [IrExpression] accessing the [it.unibo.collektive.compiler.common.CollektiveNames.AGGREGATE_CLASS_NAME]
 *   context, or `null` if none is found
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
 * Traverses this [IrExpression] subtree to find the first [IrGetValue]
 * whose type is assignable from the provided [targetType].
 *
 * Performs a depth-first search to locate a matching captured variable.
 *
 * @param targetType the IR class whose type to match
 * @return the first matching [IrGetValue], or `null` if none is found
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
