package it.unibo.collektive.examples.channel

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.alchemist.device.sensors.DistanceSensor
import it.unibo.collektive.alchemist.device.sensors.LocalSensing
import it.unibo.collektive.examples.gradient.gradient
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.plus
import kotlin.Double.Companion.POSITIVE_INFINITY

/**
 * Compute the channel between the source and the target with obstacles.
 */
context(LocalSensing, DistanceSensor)
fun Aggregate<Int>.channelWithObstacles(): Any =
    if (sense("obstacle")) {
        false
    } else {
        channel(sense("source"), sense("target"), channelWidth = 0.3)
    }

/**
 * Compute the channel between the [source] and the [target] with a specific [channelWidth].
 */
context(DistanceSensor)
fun Aggregate<Int>.channel(source: Boolean, target: Boolean, channelWidth: Double): Boolean =
    gradient(source) + gradient(target) <= distanceBetween(source, target) + channelWidth

/**
 * Compute the distance between the [source] and the [target].
 */
context(DistanceSensor)
fun Aggregate<Int>.distanceBetween(source: Boolean, target: Boolean): Double = broadcast(source, gradient(target))

/**
 * Computes the [gradientCast] from the [source] with the [value] that is the distance from the [source] to the target.
 */
context(DistanceSensor)
fun Aggregate<Int>.broadcast(source: Boolean, value: Double): Double = gradientCast(source, value) { it }

/**
 * Compute the gradient of the aggregate from the [source] to the [target].
 * The [accumulate] function is used to accumulate the value of the aggregate.
 */
context(DistanceSensor)
fun Aggregate<Int>.gradientCast(source: Boolean, initial: Double, accumulate: (Double) -> Double): Double =
    share(POSITIVE_INFINITY to initial) { field ->
        val dist = distances()
        when {
            source -> 0.0 to initial
            else -> {
                val resultField = dist.alignedMap(field) { distField, (currentDist, value) ->
                    distField + currentDist to accumulate(value)
                }
                resultField.fold(POSITIVE_INFINITY to POSITIVE_INFINITY) { acc, value ->
                    if (value.first < acc.first) value else acc
                }
            }
        }
    }.second
