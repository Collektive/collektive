/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.alignment.Path
import it.unibo.collektive.aggregate.alignment.path.IntPath
import it.unibo.collektive.aggregate.alignment.path.LongPath
import it.unibo.collektive.aggregate.alignment.path.StringPath
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.toMap
import it.unibo.collektive.testing.SerializingMailbox
import it.unibo.collektive.testing.forEachPerformanceProfile
import it.unibo.collektive.testing.serializablePerformanceProfiles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

class PathSerializationTest {

    @Serializable
    data class PathHolder(val path: Path, val byPath: Map<Path, Int>)

    private val paths: List<Path> = listOf(
        IntPath(0),
        IntPath(Int.MIN_VALUE),
        IntPath(Int.MAX_VALUE),
        LongPath(-1L),
        LongPath(Long.MIN_VALUE),
        LongPath(Long.MAX_VALUE),
        StringPath(byteArrayOf(1, 2, 3, -1)),
        StringPath(""),
    )

    @OptIn(ExperimentalSerializationApi::class)
    private val formats: List<SerialFormat> = listOf(Json, ProtoBuf)

    @OptIn(ExperimentalSerializationApi::class)
    private fun roundTrip(format: SerialFormat, holder: PathHolder): PathHolder = when (format) {
        is Json -> format.decodeFromString(
            PathHolder.serializer(),
            format.encodeToString(PathHolder.serializer(), holder),
        )
        is ProtoBuf -> format.decodeFromByteArray(
            PathHolder.serializer(),
            format.encodeToByteArray(PathHolder.serializer(), holder),
        )
        else -> error("Unsupported format $format")
    }

    @Test
    fun `every serializable path type survives a round trip preserving its type`() {
        formats.forEach { format ->
            paths.forEach { path ->
                val holder = PathHolder(path, mapOf(path to 1))
                assertEquals(holder, roundTrip(format, holder), "Round trip of $path failed with $format")
            }
        }
    }

    /*
     * Device 1 enters a branch that device 2 skips: the subsequent neighboring must still align,
     * both across the branch (the path must not depend on the history of previous alignments)
     * and across the network (the path must be deserialized as the same type it was serialized as).
     */
    private fun Aggregate<Int>.branchThenShare(): Map<Int, Int> {
        if (localId == 1) {
            neighboring(localId).local.value
        }
        return neighboring(localId * 10).neighbors.toMap()
    }

    @Test
    fun `devices exchanging serialized messages align with every serializable profile`() {
        formats.forEach { format ->
            forEachPerformanceProfile(serializablePerformanceProfiles) { profile ->
                val sender = SerializingMailbox(format)
                aggregate(1, sender, performanceProfile = profile) { branchThenShare() }
                val receiver = SerializingMailbox(format)
                receiver.receiveAndDeserialize(sender.serializeToString(2))
                val received = aggregate(2, receiver, performanceProfile = profile) { branchThenShare() }
                assertEquals(mapOf(1 to 10), received.result, "Misaligned with $format")
            }
        }
    }
}
