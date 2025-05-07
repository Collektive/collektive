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
import it.unibo.collektive.aggregate.api.CollektiveIgnore
import it.unibo.collektive.aggregate.api.YieldingContext

/**
 * Contains canonical names for aggregate-related functions, classes, and annotations.
 *
 * This object centralizes the string constants that represent:
 * - Fully-qualified names (FQNs) of DSL functions and types
 * - Function names used by the Collektive Kotlin compiler plugin
 *
 * These constants are useful for matching IR declarations, resolving symbols, and generating IR.
 */
object AggregateFunctionNames {

    /** Simple name of the [Aggregate] interface. Useful for logging and diagnostics. */
    val AGGREGATE_CLASS_NAME: String? = Aggregate::class.simpleName

    /** Fully qualified name of the [Aggregate] interface. */
    val AGGREGATE_CLASS_FQ_NAME: String = checkNotNull(Aggregate::class.qualifiedName)

    /** The package containing the Aggregate DSL API. */
    val AGGREGATE_API_PACKAGE: String = AGGREGATE_CLASS_FQ_NAME.substringBeforeLast('.')

    /** Name of the top-level [Aggregate.alignedOn] DSL function. */
    const val ALIGNED_ON_FUNCTION_NAME: String = "alignedOn"

    /** Fully qualified name of the top-level [Aggregate.alignedOn] DSL function. */
    val ALIGNED_ON_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.$ALIGNED_ON_FUNCTION_NAME"

    /** Name of the [Aggregate.align] function used to begin an alignment block. */
    val ALIGN_FUNCTION_NAME: String = Aggregate<*>::align.name

    /** Fully qualified name of the [Aggregate.align] function. */
    val ALIGN_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.$ALIGN_FUNCTION_NAME"

    /** Name of the [Aggregate.dealign] function used to close an alignment block. */
    val DEALIGN_FUNCTION_NAME: String = Aggregate<*>::dealign.name

    /** Fully-qualified name of the [Aggregate.dealign] function. */
    val DEALIGN_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.$DEALIGN_FUNCTION_NAME"

    /** Fully-qualified name of the [Field] class. Used to identify field types in IR. */
    val FIELD_CLASS_FQ_NAME: String = checkNotNull(Field::class.qualifiedName)

    /** Fully-qualified name of the [Aggregate.evolve] function. */
    val EVOLVE_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.evolve"

    /** Fully-qualified name of the [Aggregate.evolving] function. */
    val EVOLVING_FUNCTION_FQ_NAME: String = "$AGGREGATE_CLASS_FQ_NAME.evolving"

    /** Fully-qualified name of the `exchange(...)` function. */
    const val EXCHANGE_FUNCTION_FQ_NAME: String = "it.unibo.collektive.aggregate.api.exchange"

    /** Fully-qualified name of the `exchanging(...)` function. */
    const val EXCHANGING_FUNCTION_FQ_NAME: String = "it.unibo.collektive.aggregate.api.exchanging"

    /** Fully-qualified name of the `neighboring(...)` function for building neighbor views. */
    const val NEIGHBORING_FUNCTION_FQ_NAME: String = "it.unibo.collektive.aggregate.api.neighboring"

    /** Fully-qualified name of the [CollektiveIgnore] annotation used to suppress automatic alignment. */
    val IGNORE_FUNCTION_ANNOTATION_FQ_NAME: String = checkNotNull(CollektiveIgnore::class.qualifiedName)

    /** Name of the `project(...)` function for field projections. */
    const val PROJECT_FUNCTION: String = "project"

    /** Fully-qualified name of the `share(...)` function. */
    const val SHARE_FUNCTION_FQ_NAME: String = "it.unibo.collektive.aggregate.api.share"

    /** Fully-qualified name of the `sharing(...)` function for declarative data flow. */
    const val SHARING_FUNCTION_FQ_NAME: String = "it.unibo.collektive.aggregate.api.sharing"

    /** Fully-qualified name of the [YieldingContext.yielding] function within a [YieldingContext]. */
    val YIELDING_FUNCTION_FQ_NAME: String = "${checkNotNull(YieldingContext::class.qualifiedName)}.yielding"
}
