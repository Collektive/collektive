package it.unibo.alchemist.model.device

import field.Field

interface DistanceSensor {
    fun distances(): Field<Double>
}
