/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.path.Path

/**
 * TODO.
 */
interface OutboundMessage<ID : Any> {
    /**
     * TODO.
     */
    data class ToBeDefined<ID : Any, Value>(
        val default: Value,
        val overrides: Map<ID, Value> = emptyMap(),
    )

    /**
     * TODO.
     */
    fun <Value> addData(path: Path, data: ToBeDefined<ID, Value>)

    fun deliverableMessageFor(id: ID): DeliverableMessage<ID, *>

//    /**
//     * TODO.
//     */
//    companion object {
//        /**
//         * TODO.
//         */
//        internal operator fun <ID : Any> invoke(
//            expectedSize: Int,
//        ): OutboundMessage<ID> = object : OutboundMessage<ID> {
//            private val defaults: MutableMap<Path, Any?> = LinkedHashMap(expectedSize * 2)
//            private val overrides: MutableMap<ID, MutableList<Pair<Path, Any?>>> = LinkedHashMap(expectedSize * 2)
//            override fun <Value> addData(path: Path, data: ToBeDefined<ID, Value>) {
//                check(!defaults.containsKey(path)) {
//                    """
//                    Aggregate alignment clash originated at the same path: $path.
//                    Possible causes are:
//                        - compiler plugin is not enabled,
//                        - multiple aligned calls. The most likely cause is an aggregate function call within a loop without proper manual alignment.
//                    If none of the above, please open an issue at https://github.com/Collektive/collektive/issues .
//                    """.trimIndent()
//                }
//                defaults[path] = data.default
//                data.overrides.forEach { (id, value) ->
//                    val destination = overrides.getOrPut(id) { mutableListOf() }
//                    destination += path to value
//                }
//            }
//        }
//    }
}
