package it.unibo.collektive.networking

import it.unibo.collektive.path.Path
import it.unibo.collektive.path.impl.SerializablePath
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlin.collections.putAll
import kotlin.reflect.KClass

@Serializable
data class MessageWithSerializedData<ID : Any, S>(
    val senderId: ID,
    val serializedSharedData: Map<SerializablePath<S>, ByteArray>,
) {

    /**
     * Get the serialized data at the given [path], assuming the provided [serialFormat].
     *
     * This function returns `null` if the path is not present in the serialized data.
     * The return result is a [Result.Success] if the deserialization is successful,
     */
    inline operator fun <reified T> get(serialFormat: SerialFormat, path: SerializablePath<S>): Result<Message<ID>>? {
//        when (serialFormat) {
//            is StringFormat -> serialFormat.decodeFromString(serializedSharedData[path]!!)
//            else -> error("Unsupported serial format: $serialFormat")
//        }
        TODO()
    }
}

/**
 * [sharedData] received by a node from [senderId].
 */
data class Message<ID : Any>(
    val senderId: ID,
    val sharedData: Map<Path, Any?>,
)

data class SharedData<Value>(val path: Path, val neighborValue: Value)

data class SerializableData<Value>(val data: Value, val type: KClass<*>)

/**
 * An [OutboundSendOperation] represents the act of [senderId] to send (possibly custom) [Message]s
 * to its neighbors.
 */
data class OutboundSendOperation<ID : Any>(
    val expectedSize: Int,
    val senderId: ID,
) {
    /**
     * The default messages to be sent to all neighbours.
     */
    private val defaults: MutableMap<Path, SerializableData<*>> = LinkedHashMap(expectedSize * 2)

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
            sharedData =
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
    inline fun <reified Payload> addMessage(
        path: Path,
        message: SingleOutboundMessage<ID, Payload>,
    ) = addMessage(path, message, Payload::class)

    /**
     * Add a [message] to the [OutboundSendOperation].
     */
    fun <Payload> addMessage(
        path: Path,
        message: SingleOutboundMessage<ID, Payload>,
        kclass: KClass<*>,
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
        defaults[path] = SerializableData(message.default, kclass)
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
