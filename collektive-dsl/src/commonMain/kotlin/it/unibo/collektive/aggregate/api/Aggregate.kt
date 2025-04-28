/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api

import it.unibo.collektive.aggregate.Field
import kotlinx.serialization.serializer

typealias YieldingScope<Shared, Returned> =
    YieldingContext<Shared, Returned>.(Shared) -> YieldingResult<Shared, Returned>

/**
 * Represents methods intended to be used internally only.
 * The usage of these methods is discouraged and should be avoided.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class DelicateCollektiveApi

/**
 * Models the minimal set of aggregate operations.
 * Holds the [localId] of the device executing the aggregate program.
 */
interface Aggregate<ID : Any> {
    /**
     * Whether this context works only in memory.
     * If false, this context is capable of serializing and deserializing messages.
     */
    val inMemoryOnly: Boolean

    /**
     * The local [ID] of the device.
     */
    val localId: ID

    /**
     * Serialization-aware version of the [exchanging] function.
     *
     * This function is not intended to be used directly.
     * Use the [exchanging] function instead.
     */
    @Suppress("FunctionName")
    @DelicateCollektiveApi
    fun <Shared, Returned> InternalAPI.`_ serialization aware exchanging`(
        initial: Shared,
        dataSharingMethod: DataSharingMethod<Shared>,
        body: YieldingScope<Field<ID, Shared>, Returned>,
    ): Returned

    /**
     * Iteratively updates the value computing the [transform] expression at each device using the last
     * computed value or the [initial].
     */
    fun <Stored> evolve(initial: Stored, transform: (Stored) -> Stored): Stored

    /**
     * Iteratively updates the value computing the [transform] expression from a [YieldingContext]
     * at each device using the last computed value or the [initial].
     */
    fun <Stored, Returned> evolving(initial: Stored, transform: YieldingScope<Stored, Returned>): Returned

    /**
     * Serialization-aware version of the [neighboring] function.
     *
     * This function is not intended to be used directly.
     * Use the [neighboring] function instead.
     */
    @Suppress("FunctionName")
    @DelicateCollektiveApi
    fun <Shared> InternalAPI.`_ serialization aware neighboring`(
        local: Shared,
        dataSharingMethod: DataSharingMethod<Shared>,
    ): Field<ID, Shared>

    /**
     * Alignment function, which pushes in the stack the pivot, executes the body and pop the last
     * element of the stack after it is called.
     * Returns the body's return element.
     */
    fun <R> alignedOn(pivot: Any?, body: () -> R): R

    /**
     * Pushes the pivot in the alignment stack.
     */
    fun align(pivot: Any?)

    /**
     * Pops the last element of the alignment stack.
     */
    fun dealign()

    /**
     * Contains the inlined version of the [Aggregate],
     * [Aggregate.exchanging], [Aggregate.neighboring] functions.
     */
    companion object {

        /**
         * Inline access to the data serialization method of an [Aggregate].
         * This method is used to avoid building serializers for in-memory-only contexts.
         */
        inline fun <reified T> Aggregate<*>.dataSharingMethod(): DataSharingMethod<T> = when {
            inMemoryOnly -> InMemory
            else -> Serialize(serializer())
        }
    }

    /**
     * Internal API for Collektive.
     * This API is not intended to be used directly.
     * Use the [exchanging] and [neighboring] functions directly instead.
     */
    @DelicateCollektiveApi
    object InternalAPI
}
