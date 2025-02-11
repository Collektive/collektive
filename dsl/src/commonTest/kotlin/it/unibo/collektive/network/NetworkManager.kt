package it.unibo.collektive.network

import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.OutboundEnvelope
import it.unibo.collektive.path.Path
import kotlinx.serialization.KSerializer

/**
 * A fully connected virtual network.
 */
class NetworkManager {
    private var messageBuffer: Map<Int, Message<Int, *>> = emptyMap()

    /**
     * Adds the [message] to the message buffer.
     */
    fun send(
        id: Int,
        message: OutboundEnvelope<Int>,
    ) {
        val deliverableMessage = message.prepareMessageFor(id)
        messageBuffer += id to deliverableMessage
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receiveMessageFor(receiverId: Int): NeighborsData<Int> =
        object : NeighborsData<Int> {
            private val neighborDeliverableMessages by lazy { messageBuffer.filter { it.key != receiverId } }
            override val neighbors: Set<Int> get() = neighborDeliverableMessages.keys

            @Suppress("UNCHECKED_CAST")
            override fun <Value> dataAt(
                path: Path,
                kClass: KSerializer<Value>,
            ): Map<Int, Value> =
                neighborDeliverableMessages
                    .mapValues { it.value.sharedData.getOrElse(path) { NoValue } as Value }
                    .filter { it.value != NoValue }
        }

    private object NoValue
}
