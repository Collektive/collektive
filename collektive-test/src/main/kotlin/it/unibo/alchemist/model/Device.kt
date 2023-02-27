package it.unibo.alchemist.model

import field.Field

interface DistanceSensor {
    fun distances(): Field<Double>
}
