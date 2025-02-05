package it.unibo.collektive.networking

import it.unibo.collektive.path.Path
import kotlin.reflect.KClass

/**
 * Network interface for the aggregate computation.
 */
interface MessageProvider<ID : Any> {

    val requiresSerialization: Boolean

    val neighbors: Set<ID>

    fun <T> messageAt(path: Path, kClazz: KClass<*>): Map<ID, T>
}

interface MessageDeliverer<ID : Any> {

    /**
     * Sends a [message] of type [OutboundSendOperation] to the neighbours.
     */
    fun deliver(message: OutboundSendOperation<ID>)

}
