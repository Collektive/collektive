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
import it.unibo.collektive.aggregate.api.YieldingContext

/**
 * The names of the aggregate functions used in the plugin.
 */
object AggregateFunctionNames {
    /**
     * The simple name of the aggregate class.
     */
    val AGGREGATE_CLASS_NAME = Aggregate::class.simpleName

    /**
     * The FQ name of the aggregate class.
     */
    val AGGREGATE_CLASS_FQ_NAME = checkNotNull(Aggregate::class.qualifiedName)

    /**
     * The simple name of the function that is used to align a block.
     */
    const val ALIGNED_ON_FUNCTION_NAME = "alignedOn"

    /**
     * The FQ name of the function that is used to align a block.
     */
    val ALIGNED_ON_FUNCTION_FQ_NAME = "$AGGREGATE_CLASS_FQ_NAME.$ALIGNED_ON_FUNCTION_NAME"

    /**
     * The simple name of the function that is used to start an alignment delimitation.
     */
    val ALIGN_FUNCTION_NAME = Aggregate<*>::align.name

    /**
     * The FQ name of the function that is used to start an alignment delimitation.
     */
    val ALIGN_FUNCTION_FQ_NAME = "$AGGREGATE_CLASS_FQ_NAME.$ALIGN_FUNCTION_NAME"

    /**
     * The name of the function that is used to stop an alignment delimitation.
     */
    val DEALIGN_FUNCTION_NAME = Aggregate<*>::dealign.name

    /**
     * The FQ name of the function that is used to stop an alignment delimitation.
     */
    val DEALIGN_FUNCTION_FQ_NAME = "$AGGREGATE_CLASS_FQ_NAME.$DEALIGN_FUNCTION_NAME"

    /**
     * The FQ name of the field class.
     */
    val FIELD_CLASS = checkNotNull(Field::class.qualifiedName)

    /**
     * The name of the function that is used to project the fields.
     */
    const val PROJECT_FUNCTION = "project"

    /**
     * The FQ name of the `neighboring` function.
     */
    const val NEIGHBORING_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.neighboring"

    /**
     * The FQ name of the `exchange` function.
     */
    const val EXCHANGE_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.exchange"

    /**
     * The FQ name of the `share` function.
     */
    const val SHARE_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.share"

    /**
     * The FQ name of the `evolve` function.
     */
    val EVOLVE_FUNCTION_FQ_NAME = "$AGGREGATE_CLASS_FQ_NAME.evolve"

    /**
     * The FQ name of the `exchanging` function.
     */
    const val EXCHANGING_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.exchanging"

    /**
     * The FQ name of the `sharing` function.
     */
    const val SHARING_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.sharing"

    /**
     * The FQ name of the `evolving` function.
     */
    val EVOLVING_FUNCTION_FQ_NAME = "${AGGREGATE_CLASS_FQ_NAME}.evolving"

    /**
     * The FQ name of the `yielding` function.
     */
    val YIELDING_FUNCTION_FQ_NAME = "${checkNotNull(YieldingContext::class.qualifiedName)}.yielding"
}
