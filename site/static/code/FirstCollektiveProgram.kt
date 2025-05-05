/**
 * Count the number of devices in the network.
 * The total is accumulated in the device with [localId] 0 (the [sink]).
 * Other devices hold the number of devices in their subtree towards the network edge, inlcuding themselves.
 * A leaf node (with no outward neighbors) will hold 1.
 */
fun Aggregate<Int>.countDevicesEntrypoint(): Int = countDevices(sink = localId == 0)

/**
 * Broadcast from the [source] the number of devices connected to the network.
 * Requires a [distances] field to be used as metric for computing the distance from the source to the target.
 */
fun Aggregate<Int>.broadcastCountDevices(distances: Field<Int, Double>, source: Boolean): Int =
    gradientCast(
        metric = distances,
        source = source,
        local = countDevices(sink = source),
    )

/**
 * [collektiveDevice] represents the device running the Collektive program.
 * It is used to access the device's properties and methods,
 * such as the [distances] method, which returns a field of distances from the neighboring nodes.
 * In this case, the source is the device with [localId] equal to 0.
 */
fun Aggregate<Int>.broadcastCountDevicesEntrypoint(collektiveDevice: CollektiveDevice<*>): Int =
    broadcastCountDevices(
        distances = with(collektiveDevice) { distances() },
        source = localId == 0,
    )

/**
 * Counts and broadcast the number of devices in the network using a leader election.
 * If the network is segmented, each connected component elects a leader to act as root.
 */
fun Aggregate<Int>.broadcastCountDevicesWithLeaderElectionEntrypoint(
    collektiveDevice: CollektiveDevice<*>,
): Int {
    val leaderId = boundedElection(bound)
    return broadcastDevices(
        distances = with(collektiveDevice) { distances() },
        source = localId == leaderId,
    )
}
