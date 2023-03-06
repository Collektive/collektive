import field.*
import it.unibo.alchemist.model.DistanceSensor
import it.unibo.alchemist.model.api.CollektiveDevice
import stack.Path

class Aggregate(private val node: CollektiveDevice<*>) {
    private val nodeId = node.node.id
    fun entrypoint() = aggregate(
        IntId(nodeId),
        node.receive(),
        emptyMap<Path, Any>()
    ) {
        gradient(nodeId == 0, node)
    }
}

fun AggregateContext.gradient(source: Boolean, sensor: DistanceSensor) =
    sharing(Double.POSITIVE_INFINITY) { data ->
        val paths: Field<Double> = sensor.distances() + data
        val others: Map.Entry<ID, Double>? = paths.min(includingSelf = false) // field to map, excluding local
        when {
            source -> 0.0
            others == null -> Double.POSITIVE_INFINITY
            else -> others.value
        }
    }