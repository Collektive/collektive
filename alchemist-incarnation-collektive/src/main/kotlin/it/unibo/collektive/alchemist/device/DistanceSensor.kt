package it.unibo.collektive.alchemist.device

import it.unibo.collektive.field.Field

/**
 * Distance sensor.
 */
interface DistanceSensor {
    /**
     * The distances from the current node to all the other nodes.
     */
    fun distances(): Field<Double>
}
