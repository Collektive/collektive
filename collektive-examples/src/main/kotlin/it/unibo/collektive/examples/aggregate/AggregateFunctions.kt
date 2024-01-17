package it.unibo.collektive.examples.aggregate

import it.unibo.collektive.ID
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboring
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.alchemist.device.DistanceSensor
import it.unibo.collektive.field.Field.Companion.hoodInto
import it.unibo.collektive.field.plus
import kotlin.Double.Companion.POSITIVE_INFINITY

/**
 * TODO.
 */
fun Aggregate.neighborCounter(): Int = neighboring(1).hoodInto(0) { acc, _ -> acc + 1 }

/**
 * TODO.
 */
context(DistanceSensor)
fun Aggregate.gradient(id: ID): Double {
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
fun Aggregate.entrypoint(): Double = gradient(localId)
