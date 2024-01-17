package it.unibo.collektive.examples.aggregate

import it.unibo.collektive.ID
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.AggregateContext
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.aggregate.ops.share
import it.unibo.collektive.alchemist.device.DistanceSensor
import it.unibo.collektive.field.Field.Companion.hoodInto
import it.unibo.collektive.field.plus
import kotlin.Double.Companion.POSITIVE_INFINITY

/**
 * TODO.
 */
fun AggregateContext.neighborCounter(): Int = neighbouring(1).hoodInto(0) { acc, _ -> acc + 1 }

/**
 * TODO.
 */
context(DistanceSensor)
fun AggregateContext.gradient(id: ID): Double {
    val paths = distances()
    return share(POSITIVE_INFINITY) { dist ->
        val minByPath: Double = (paths + dist).excludeSelf().map { it.value }.minOrNull() ?: POSITIVE_INFINITY
        if (id == IntId(0)) {
            0.0
        } else {
            minByPath
        }
    }
}

/**
 * TODO.
 */
context(DistanceSensor)
fun AggregateContext.entrypoint(): Double = gradient(localId)
