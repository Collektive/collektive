/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.path.Path
import kotlin.reflect.KClass

/**
 * TODO.
 */
interface InboundMessage<ID : Any> {
    /**
     * TODO.
     */
    val neighbors: Set<ID>
    /**
     * TODO.
     */
    fun <Value> dataAt(path: Path, kClass: KClass<*>): Map<ID, Value>

//    /**
//     * TODO.
//     */
//    companion object {
//        internal operator fun <ID : Any> invoke(
//            messages: Collection<DeliverableMessage<ID, *>>,
//        ): InboundMessage<ID> = object : InboundMessage<ID> {
//            @Suppress("UNCHECKED_CAST")
//            override fun <Value> dataAt(
//                path: Path,
//                kClass: KClass<*>
//            ): Map<ID, Value> = messages
//                .mapNotNull { it.payload[path] }
//                .map { it as Value }
//                .mapIndexed { index, value -> messages.elementAt(index).senderId to value }
//                .toMap()
//
//        }
//        /**
//         * TODO.
//         */
//        inline fun <ID : Any, reified Value> InboundMessage<ID>.dataAt(path: Path): Map<ID, Value> =
//            dataAt(path, Value::class)
//    }
}

class EmptyInboundMessage<ID : Any> : InboundMessage<ID> {
    override val neighbors: Set<ID> = emptySet()
    override fun <Value> dataAt(path: Path, kClass: KClass<*>): Map<ID, Value> = emptyMap()
}

