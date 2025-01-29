package it.unibo.collektive.networking

import it.unibo.collektive.path.Path
import it.unibo.collektive.serialization.MapAnySerializer
import kotlinx.serialization.Serializable
import kotlin.collections.putAll

/**
 * [messages] received by a node from [senderId].
 */
@Serializable
data class Message<ID : Any>(
    val senderId: ID,
    @Serializable(with = MapAnySerializer::class)
    val messages: Map<Path, Any?>,
)

/**
 * An [OutboundSendOperation] represents the act of [senderId] to send (possibly custom) [Message]s
 * to its neighbors.
 */
data class OutboundSendOperation<ID : Any>(
    private val expectedSize: Int,
    val senderId: ID,
) {
    /**
     * The default messages to be sent to all neighbours.
     */
    val defaults: MutableMap<Path, Any?> = LinkedHashMap(expectedSize * 2)

    private val overrides: MutableMap<ID, MutableList<Pair<Path, Any?>>> = LinkedHashMap(expectedSize * 2)

    /**
     * Check if the [OutboundSendOperation] is empty.
     */
    fun isEmpty(): Boolean = defaults.isEmpty()

    /**
     * Check if the [OutboundSendOperation] is not empty.
     */
    fun isNotEmpty(): Boolean = defaults.isNotEmpty()

    /**
     * Returns the messages for device [id].
     */
    fun messagesFor(id: ID): Message<ID> =
        Message(
            senderId = senderId,
            messages =
                LinkedHashMap<Path, Any?>(
                    defaults.size + overrides.size,
                    1.0f,
                ).also { result ->
                    result.putAll(defaults)
                    overrides[id]?.let { result.putAll(it) }
                },
        )

    /**
     * Add a [message] to the [OutboundSendOperation].
     */
    fun <Payload> addMessage(
        path: Path,
        message: SingleOutboundMessage<ID, @Serializable Payload>,
    ) {
        check(!defaults.containsKey(path)) {
            """
            Aggregate alignment clash originated at the same path: $path. 
            Possible causes are: 
                - compiler plugin is not enabled,
                - multiple aligned calls. The most likely cause is an aggregate function call within a loop without proper manual alignment.
            If none of the above, please open an issue at https://github.com/Collektive/collektive/issues .
            """.trimIndent()
        }
        defaults[path] = message.default
        message.overrides.forEach { (id, value) ->
            val destination = overrides.getOrPut(id) { mutableListOf() }
            destination += path to value
        }
    }
}

/**
 * A [SingleOutboundMessage] contains the values associated to a [Path] in the messages of [OutboundSendOperation].
 * Has a [default] value that is sent regardless the awareness the device's neighbours, [overrides] specifies the
 * payload depending on the neighbours' values.
 */
@Serializable
data class SingleOutboundMessage<ID : Any, Payload>(
    val default: Payload,
    val overrides: Map<ID, Payload> = emptyMap(),
)
