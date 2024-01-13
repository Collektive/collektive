package it.unibo.collektive.examples.aggregate

import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.AggregateContext
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.aggregate.ops.share
import it.unibo.collektive.alchemist.device.DistanceSensor
import it.unibo.collektive.field.Field.Companion.hoodInto
import it.unibo.collektive.field.plus

/**
 * TODO.
 */
fun AggregateContext.neighborCounter(): Int = neighbouring(1).hoodInto(0) { acc, _ -> acc + 1 }

/**
 * TODO.
 */
context(DistanceSensor)
fun AggregateContext.gradient(id: IntId): Double {
    val paths = distances()
    return share(Double.POSITIVE_INFINITY) { dist ->
        val minByPath = (paths + dist).hoodInto(Double.POSITIVE_INFINITY) { acc, value -> acc.coerceAtMost(value) }
        if (id == IntId(0)) {
            0.0
        } else {
            minByPath
        }
    }
}
