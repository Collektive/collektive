package my.test

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.field.operations.min
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import kotlin.Double.Companion.POSITIVE_INFINITY

fun Aggregate<Int>.gradient(distanceSensor: CollektiveDevice<*>, source: Boolean): Double =
    share(POSITIVE_INFINITY) {
        val dist = distanceSensor.run { distances() }
        when {
            source -> 0.0
            else -> (it + dist).min(POSITIVE_INFINITY)
        }
    }
