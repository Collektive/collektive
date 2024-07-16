package it.unibo.collektive

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.operations.min
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus

/**
 * Gradient-based operators.
 */
object GradientStdlib {
    /**
     * Compute the gradient using the [metric] function starting from the [source] node.
     */
    fun <ID : Any> Aggregate<ID>.gradient(source: Boolean, metric: () -> Field<ID, Double>): Double {
        return share(Double.POSITIVE_INFINITY) {
            val updatedDistances = metric() + it
            when {
                source -> 0.0
                else -> updatedDistances.min(Double.POSITIVE_INFINITY)
            }
        }
    }
}
