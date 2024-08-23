package my.test

import it.unibo.alchemist.collektive.device.DistanceSensor
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.operations.min
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import kotlin.Double.Companion.POSITIVE_INFINITY

fun Aggregate<Int>.gradient(sensor: DistanceSensor, source: Boolean): Double =
    share(POSITIVE_INFINITY) {
        sensor.run {
            val dist = distances()
            when {
                source -> 0.0
                else -> (it + dist).min(POSITIVE_INFINITY)
            }
        }
    }
