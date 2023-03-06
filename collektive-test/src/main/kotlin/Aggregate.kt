import field.*
import it.unibo.alchemist.model.device.DistanceSensor
import it.unibo.alchemist.model.device.CollektiveDevice
import stack.Path

class Aggregate(private val node: CollektiveDevice<*>) {
    private val nodeId = node.node.id
    private var state = emptyMap<Path, Any?>()
    fun entrypoint() = aggregate(IntId(nodeId), node.receive(), state) {
        gradient(nodeId == 0, node)
    }.also { state = it.newState }
}

fun AggregateContext.gradient(source: Boolean, sensor: DistanceSensor) =
    sharing(Double.POSITIVE_INFINITY) { distances ->
        val paths: Field<Double> = sensor.distances() + distances
        val minByPath = paths.min(includingSelf = false)?.value // field to map, excluding local
        when {
            source -> 0.0
            minByPath == null -> Double.POSITIVE_INFINITY
            else -> minByPath
        }
    }
