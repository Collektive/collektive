package it.unibo.collektive.examples.branching

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.alchemist.device.sensors.LocalSensing
import it.unibo.collektive.field.Field.Companion.hood

/**
 * A simple example of branching.
 */
context(LocalSensing)
fun Aggregate<Int>.branching() =
    if (localId % 2 == 0) {
        neighboringViaExchange(1).hood(0) { acc, _ -> acc + 1 }
    } else {
        0
    }
