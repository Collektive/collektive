package my.test

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.alchemist.device.DistanceSensor
import it.unibo.collektive.field.min
import it.unibo.collektive.field.plus
import kotlin.Double.Companion.POSITIVE_INFINITY

context(DistanceSensor)
fun Aggregate<Int>.gradient(source: Boolean): Double =
    share(POSITIVE_INFINITY) {
        val dist = distances()
        when {
            source -> 0.0
            else -> (it + dist).min(POSITIVE_INFINITY)
        }
    }
