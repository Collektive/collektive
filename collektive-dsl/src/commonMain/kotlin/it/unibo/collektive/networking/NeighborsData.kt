/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.path.Path

/**
 * Data received from neighbors.
 */
interface NeighborsData<ID : Any> {
    /**
     * The set of known neighbors.
     */
    val neighbors: Set<ID>

    /**
     * The data from all neighbors at a specific [path].
     */
    fun <Value> dataAt(path: Path, dataSharingMethod: DataSharingMethod<Value>): Map<ID, Value>
}

/**
 * An empty inbound message.
 */
class NoNeighborsData<ID : Any> : NeighborsData<ID> {
    override val neighbors: Set<ID> = emptySet()

    override fun <Value> dataAt(path: Path, dataSharingMethod: DataSharingMethod<Value>): Map<ID, Value> = emptyMap()
}
