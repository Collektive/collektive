import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.stdlib.accumulation.countDevices
import it.unibo.collektive.stdlib.consensus.boundedElection
import it.unibo.collektive.stdlib.spreading.gradientCast

/**
 * Count the number of devices in the network.
 * The total is accumulated in the device with [localId] 0 (the sink).
 * Other devices hold the number of devices in their subtree, including themselves:
 * a leaf of the tree holds 1.
 */
fun Aggregate<Int>.countDevicesEntrypoint(): Int = countDevices(sink = localId == 0)

/**
 * Broadcast from the [source] the number of devices connected to the network,
 * using [distances] as the metric to compute the distance from the source.
 */
fun Aggregate<Int>.broadcastCountDevices(distances: Field<Int, Double>, source: Boolean): Int = gradientCast(
    metric = distances,
    source = source,
    local = countDevices(sink = source),
)

/**
 * [collektiveDevice] represents the simulated device running the Collektive program.
 * It provides access to the device's properties and methods,
 * such as [CollektiveDevice.distances], which returns a field of distances from the neighboring devices.
 * In this case, the source is the device with [localId] equal to 0.
 */
fun Aggregate<Int>.broadcastCountDevicesEntrypoint(collektiveDevice: CollektiveDevice<*>): Int = broadcastCountDevices(
    distances = with(collektiveDevice) { distances() },
    source = localId == 0,
)

/**
 * Count and broadcast the number of devices in the network using a leader election.
 * If the network is segmented, each connected component elects a leader to act as root.
 * Note that [env] is used only to show in the simulator which devices are leaders.
 */
fun Aggregate<Int>.broadcastCountDevicesWithLeaderElectionEntrypoint(
    collektiveDevice: CollektiveDevice<*>,
    env: EnvironmentVariables,
): Int {
    val isLeader = (findLeader() == localId).also { env["isSource"] = it }
    return broadcastCountDevices(
        distances = with(collektiveDevice) { distances() },
        source = isLeader,
    )
}

/**
 * Elect a leader for each area of (at most) 25 hops: the device with the highest ID in the area wins.
 */
fun Aggregate<Int>.findLeader(): Int = boundedElection(bound = 25)
