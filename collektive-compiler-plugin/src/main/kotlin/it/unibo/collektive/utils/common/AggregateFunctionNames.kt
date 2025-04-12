/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.utils.common

/**
 * The names of the aggregate functions used in the plugin.
 */
object AggregateFunctionNames {
    /**
     * The simple name of the function that is used to align a block.
     */
    const val ALIGNED_ON_FUNCTION_NAME = "alignedOn"

    /**
     * The FQ name of the function that is used to align a block.
     */
    const val ALIGNED_ON_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.alignedOn"

    /**
     * The simple name of the function that is used to start an alignment delimitation.
     */
    const val ALIGN_FUNCTION_NAME = "align"

    /**
     * The FQ name of the function that is used to start an alignment delimitation.
     */
    const val ALIGN_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.align"

    /**
     * The name of the function that is used to stop an alignment delimitation.
     */
    const val DEALIGN_FUNCTION_NAME = "dealign"

    /**
     * The FQ name of the function that is used to stop an alignment delimitation.
     */
    const val DEALIGN_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.dealign"

    /**
     * The simple name of the aggregate class.
     */
    const val AGGREGATE_CLASS_NAME = "Aggregate"

    /**
     * The FQ name of the aggregate class.
     */
    const val AGGREGATE_CLASS_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate"

    /**
     * The FQ name of the field class.
     */
    const val FIELD_CLASS = "it.unibo.collektive.field.Field"

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
    const val EVOLVE_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.evolve"

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
    const val EVOLVING_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.Aggregate.evolving"

    /**
     * The FQ name of the `yielding` function.
     */
    const val YIELDING_FUNCTION_FQ_NAME = "it.unibo.collektive.aggregate.api.YieldingContext.yielding"
}
