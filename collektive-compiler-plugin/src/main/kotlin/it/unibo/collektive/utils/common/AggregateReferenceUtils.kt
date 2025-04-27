/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.utils.common

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.PurelyLocal
import it.unibo.collektive.utils.common.AggregateFunctionNames.PURELY_LOCAL_ANNOTATION_FQ_NAME
import it.unibo.collektive.utils.logging.debug
import it.unibo.collektive.utils.logging.debugPrint
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.parents
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid

/**
 * Builds an [IrFunctionAccessExpression] that accesses the `context` property of a [Field].
 *
 * This utility constructs an IR call to the `context` getter on an [IrGetValue]
 * that references a [it.unibo.collektive.aggregate.Field] instance.
 *
 * @receiver an [IrGetValue] referencing a [it.unibo.collektive.aggregate.Field]
 * @param pluginContext the IR plugin context used to build the IR
 * @param aggregateClass the IR class representing the `Aggregate<ID>` interface
 * @param fieldClass the IR class representing the `Field<ID, *>` interface
 * @param getContext the IR function corresponding to the `context` property getter
 * @return an [IrFunctionAccessExpression] that retrieves the field's context
 * @throws IllegalStateException if the receiver is not assignable from [Field]
 */
private fun IrGetValue.buildGetFieldContext(
    pluginContext: IrPluginContext,
    aggregateClass: IrClass,
    fieldClass: IrClass,
    getContext: IrFunction,
): IrFunctionAccessExpression {
    check(type.isAssignableFrom(fieldClass.defaultType)) {
        "Expected a Field, but got a: ${type.classFqName}"
    }
    return IrSingleStatementBuilder(pluginContext, Scope(getContext.symbol), startOffset, endOffset)
        .build {
            irCall(getContext.symbol, aggregateClass.defaultType)
                .apply { dispatchReceiver = this@buildGetFieldContext }
        }
}

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
fun IrExpression.findAggregateReference(
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
private fun IrExpression.findFirstCapturedVariableOfType(targetType: IrClass): IrGetValue? {
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

/**
 * Determines whether this [IrFunction] represents an aggregate-aware DSL operation.
 *
 * A function is considered aggregate-aware if:
 * - It operates on an [Aggregate] or a [Field] via receiver or parameters
 * - It is **not** annotated with [PurelyLocal]
 *
 * This is used during the alignment phase to detect functions
 * that should be transformed into aggregate computations.
 *
 * @param aggregateClass the IR class for `Aggregate<ID>`
 * @param fieldClass the IR class for `Field<ID, *>`
 * @param logger an optional [MessageCollector] for debug logging
 * @return `true` if the function is aggregate-aware, `false` otherwise
 */
fun IrFunction.isAggregate(aggregateClass: IrClass, fieldClass: IrClass, logger: MessageCollector? = null): Boolean =
    !isAnnotatedAsPurelyLocal(logger) &&
        listOf(aggregateClass, fieldClass).any { irClass: IrClass ->
            val type = irClass.defaultType
            extensionReceiverParameter?.type?.isAssignableFrom(type)
                ?: dispatchReceiverParameter?.type?.isAssignableFrom(type)
                ?: valueParameters.any { it.type.isAssignableFrom(type) }
        }

/**
 * Checks whether this [IrFunction] or any of its enclosing declarations
 * are annotated with [PurelyLocal].
 *
 * Functions or classes marked with [PurelyLocal] are excluded
 * from alignment and treated as purely local computations.
 *
 * @param logger an optional [MessageCollector] for debug output
 * @return `true` if the function or a parent is annotated with [PurelyLocal]
 */
fun IrFunction.isAnnotatedAsPurelyLocal(logger: MessageCollector? = null): Boolean {
    val allAnnotations = annotations + parents.flatMap { (it as? IrAnnotationContainer)?.annotations.orEmpty() }
    logger?.debug("Detected annotations: $allAnnotations")
    return allAnnotations.any { it.type.classFqName?.asString() == PURELY_LOCAL_ANNOTATION_FQ_NAME }
}
