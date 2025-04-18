package my.test

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import it.unibo.collektive.stdlib.fields.minValue
import kotlin.Double.Companion.POSITIVE_INFINITY

fun Aggregate<Int>.gradient(distanceSensor: CollektiveDevice<*>, source: Boolean): Double =
    share(POSITIVE_INFINITY) {
        val dist = distanceSensor.run { distances() }
        when {
            source -> 0.0
            else -> (it + dist).minValue(POSITIVE_INFINITY)
        }
    }
