import field.min
import field.plus
import it.unibo.alchemist.model.DistanceSensor
import it.unibo.alchemist.model.api.CollektiveDevice

class Aggregate(private val node: CollektiveDevice<*>) {
    fun entrypoint() = aggregate {
        gradient(node.node.id == 0, node)
    }
}

fun AggregateContext.gradient(source: Boolean, sensor: DistanceSensor) {
    sharing (Double.POSITIVE_INFINITY) { distance ->
        val newDistance = distance + sensor.distances()
        if (source) 0 else newDistance.min(includingSelf = false)
    }
}