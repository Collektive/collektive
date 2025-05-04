/**
 * Count the number of devices in the network.
 * The total is accumulated in the device with [localId] 0
 * (where the [sink] is true).
 * Other devices will have a value that represents the number of devices
 * from self (included) to the closest edge of the network.
 */
fun Aggregate<Int>.deviceCounter(): Int = countDevices(sink = localId == 0)

/**
 * Broadcast from the [source] the number of devices connected in the network.
 * Need a [distances] field used as metric to compute the distance from the source to the target.
 */
fun Aggregate<Int>.broadcastDevices(distances: Field<Int, Double>, source: Boolean): Int =
    gradientCast(
        metric = distances,
        source = source,
        local = countDevices(sink = source),
    )

/**
 * [collektiveDevice] is a representation of the device that runs a Collektive program.
 * It is used to access the device's properties and methods,
 * such as the [distances] method, which returns a field of distances from the neighboring nodes.
 * In this case, the source is the device with [localId] 0.
 */
fun Aggregate<Int>.broadcastDevicesEntrypoint(collektiveDevice: CollektiveDevice<*>): Int =
    broadcastDevices(
        distances = with(collektiveDevice) { distances() },
        source = localId == 0,
    )

/**
 * Broadcast the number of devices connected in the network with a leader election.
 */
fun Aggregate<Int>.broadcastDevicesWithLeaderElectionEntrypoint(
    collektiveDevice: CollektiveDevice<*>,
): Int {
    val leaderId = boundedElection(bound)
    return broadcastDevices(
        distances = with(collektiveDevice) { distances() },
        source = localId == leaderId,
    )
}
