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
import it.unibo.collektive.aggregate.api.CollektiveIgnore
import it.unibo.collektive.compiler.common.CollektiveNames.IGNORE_FUNCTION_ANNOTATION_FQ_NAME
import it.unibo.collektive.compiler.common.debug
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.parents

/**
 * Returns `true` if this [IrFunction] is abstract (declared but not implemented).
 */
val IrFunction.isAbstract get() = this is IrSimpleFunction && modality == Modality.ABSTRACT

/**
 * Returns `true` if this [IrFunction] is concrete (i.e., implemented, not abstract).
 */
val IrFunction.isConcrete get() = !isAbstract

/**
 * Checks whether this [IrFunction] or any of its enclosing declarations
 * are annotated with [CollektiveIgnore].
 *
 * Functions or classes marked with [CollektiveIgnore] are excluded
 * from alignment and treated as purely local computations.
 *
 * @param logger an optional [MessageCollector] for debug output
 * @return `true` if the function or a parent is annotated with [CollektiveIgnore]
 */
internal fun IrFunction.hasAnnotationDisablingPlugin(logger: MessageCollector? = null): Boolean {
    val allAnnotations = annotations + parents.flatMap { (it as? IrAnnotationContainer)?.annotations.orEmpty() }
    logger?.debug("Detected annotations: $allAnnotations")
    return allAnnotations.any { it.type.classFqName?.asString() == IGNORE_FUNCTION_ANNOTATION_FQ_NAME }
}

/**
 * Determines whether this [IrFunction] represents an aggregate-aware DSL operation.
 *
 * A function is considered aggregate-aware if:
 * - It operates on an [Aggregate] or a [Field] via receiver or parameters
 * - It is **not** annotated with [CollektiveIgnore]
 *
 * This is used during the alignment phase to detect functions
 * that should be transformed into aggregate computations.
 *
 * @param aggregateClass the IR class for `Aggregate<ID>`
 * @param fieldClass the IR class for `Field<ID, *>`
 * @param logger an optional [MessageCollector] for debug logging
 * @return `true` if the function is aggregate-aware, `false` otherwise
 */
internal fun IrFunction.isAggregate(
    aggregateClass: IrClass,
    fieldClass: IrClass,
    logger: MessageCollector? = null,
): Boolean = !hasAnnotationDisablingPlugin(logger) &&
    listOf(aggregateClass, fieldClass).any { irClass: IrClass ->
        val type = irClass.defaultType
        extensionReceiverParameter?.type?.isAssignableFrom(type)
            ?: dispatchReceiverParameter?.type?.isAssignableFrom(type)
            ?: valueParameters.any { it.type.isAssignableFrom(type) }
    }
