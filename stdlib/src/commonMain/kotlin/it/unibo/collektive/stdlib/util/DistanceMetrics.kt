/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.field.Field
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * A 2D point represented as a pair of [x] and [y] [coordinates].
 */
@JvmInline
@Serializable
value class Point2D(val coordinates: Pair<Double, Double>) {
    val x: Double get() = coordinates.first
    val y: Double get() = coordinates.second

    /**
     * The [x] coordinate.
     */
    operator fun component1(): Double = x

    /**
     * The [y] coordinate.
     */
    operator fun component2(): Double = y

    /**
     * The sum of two [Point2D] coordinates.
     */
    operator fun plus(other: Point2D): Point2D = Point2D(x + other.x to y + other.y)

    /**
     * The difference of two [Point2D] coordinates.
     */
    operator fun minus(other: Point2D): Point2D = Point2D(x - other.x to y - other.y)

    /**
     * The product of a [Point2D] coordinate and a scalar.
     */
    operator fun times(scalar: Double): Point2D = Point2D(x * scalar to y * scalar)

    /**
     * The division of a [Point2D] coordinate by a scalar.
     */
    operator fun div(scalar: Double): Point2D = Point2D(x / scalar to y / scalar)

    /**
     * The negation of a [Point2D] coordinate.
     */
    operator fun unaryMinus(): Point2D = Point2D(-x to -y)

    /**
     * The unary plus of a [Point2D] coordinate.
     */
    operator fun unaryPlus(): Point2D = this

    /**
     * The distance from this [Point2D] to another [Point2D].
     */
    fun distanceTo(other: Point2D): Double = hypot(x - other.x, y - other.y)
}

/**
 * A 3D point represented as a triple of [x], [y], and [z] [coordinates].
 */
@JvmInline
@Serializable
value class Point3D(private val coordinates: Triple<Double, Double, Double>) {
    /**
     * The [x] coordinate.
     */
    val x: Double get() = coordinates.first

    /**
     * The [y] coordinate.
     */
    val y: Double get() = coordinates.second

    /**
     * The [z] coordinate.
     */
    val z: Double get() = coordinates.third

    /**
     * The [x] coordinate.
     */
    operator fun component1(): Double = x

    /**
     * The [y] coordinate.
     */
    operator fun component2(): Double = y

    /**
     * The [z] coordinate.
     */
    operator fun component3(): Double = z

    /**
     * The sum of two [Point3D] coordinates.
     */
    operator fun plus(other: Point3D): Point3D = Point3D(Triple(x + other.x, y + other.y, z + other.z))

    /**
     * The difference of two [Point3D] coordinates.
     */
    operator fun minus(other: Point3D): Point3D = Point3D(Triple(x - other.x, y - other.y, z - other.z))

    /**
     * The product of a [Point3D] coordinate and a scalar.
     */
    operator fun times(scalar: Double): Point3D = Point3D(Triple(x * scalar, y * scalar, z * scalar))

    /**
     * The division of a [Point3D] coordinate by a scalar.
     */
    operator fun div(scalar: Double): Point3D = Point3D(Triple(x / scalar, y / scalar, z / scalar))

    /**
     * The negation of a [Point3D] coordinate.
     */
    operator fun unaryMinus(): Point3D = Point3D(Triple(-x, -y, -z))

    /**
     * The unary plus of a [Point3D] coordinate.
     */
    operator fun unaryPlus(): Point3D = this

    /**
     * The distance from this [Point3D] to another [Point3D].
     */
    fun distanceTo(other: Point3D): Double {
        val xDist = x - other.x
        val yDist = y - other.y
        val zDist = z - other.z
        return sqrt(xDist * xDist + yDist * yDist + zDist * zDist)
    }
}

/**
 * Returns a field containing 1 for each aligned neighbor.
 */
fun <ID : Any> Aggregate<ID>.hops(): Field<ID, Int> = neighboring(0.toByte()).mapWithId { id, _ ->
    when {
        id == localId -> 0
        else -> 1
    }
}

/**
 * Returns a field containing the Euclidean distance from the local node to each neighbor.
 *
 * @param extractPosition a function that extracts the position of a node given its ID.
 */
@OptIn(ExperimentalContracts::class)
inline fun <ID : Any> Aggregate<ID>.euclideanDistance2D(
    crossinline extractPosition: (ID) -> Point2D,
): Field<ID, Double> {
    contract { callsInPlace(extractPosition, InvocationKind.EXACTLY_ONCE) }
    val myPosition = extractPosition(localId)
    return neighboring(myPosition).map { it.distanceTo(myPosition) }
}

/**
 * Returns a field containing the Euclidean distance from the local node to each neighbor.
 *
 * @param localPosition a function that extracts the position of a node given its ID.
 */
fun <ID : Any> Aggregate<ID>.euclideanDistance2D(localPosition: Point2D): Field<ID, Double> =
    neighboring(localPosition).map { it.distanceTo(localPosition) }

/**
 * Returns a field containing the Euclidean distance from the local node to each neighbor.
 *
 * @param localPosition a function that extracts the position of a node given its ID.
 */
fun <ID : Any> Aggregate<ID>.euclideanDistance2D(localPosition: Pair<Double, Double>): Field<ID, Double> =
    Point2D(localPosition).let { neighboring(it).map { other -> other.distanceTo(it) } }

/**
 * Returns a field containing the Euclidean distance from the local node to each neighbor.
 *
 * @param extractPosition a function that extracts the position of a node given its ID.
 */
@OptIn(ExperimentalContracts::class)
inline fun <ID : Any> Aggregate<ID>.euclideanDistance3D(
    crossinline extractPosition: (ID) -> Point3D,
): Field<ID, Double> {
    contract { callsInPlace(extractPosition, InvocationKind.EXACTLY_ONCE) }
    val myPosition = extractPosition(localId)
    return neighboring(myPosition).map { it.distanceTo(myPosition) }
}

/**
 * Returns a field containing the Euclidean distance from the local node to each neighbor.
 *
 * @param localPosition a function that extracts the position of a node given its ID.
 */
fun <ID : Any> Aggregate<ID>.euclideanDistance3D(localPosition: Point3D): Field<ID, Double> =
    neighboring(localPosition).map { it.distanceTo(localPosition) }

/**
 * Returns a field containing the Euclidean distance from the local node to each neighbor.
 *
 * @param localPosition a function that extracts the position of a node given its ID.
 */
fun <ID : Any> Aggregate<ID>.euclideanDistance3D(localPosition: Triple<Double, Double, Double>): Field<ID, Double> =
    Point3D(localPosition).let { neighboring(it).map { other -> other.distanceTo(it) } }
