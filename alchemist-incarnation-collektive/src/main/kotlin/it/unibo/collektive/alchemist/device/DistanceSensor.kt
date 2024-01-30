package it.unibo.collektive.alchemist.device

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.field.Field

/**
 * Distance sensor.
 */
interface DistanceSensor {
    /**
     * The distances from the current node to all the other nodes.
     */
    fun <ID : Any> Aggregate<ID>.distances(): Field<ID, Double>
}
