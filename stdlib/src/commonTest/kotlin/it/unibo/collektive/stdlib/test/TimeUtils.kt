/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

object TimeUtils {
    private var timeOfLastReplica: Instant? = null

    fun getAbsoluteTime(): Instant =
        Clock.System.now()

    fun getDeltaTime(): Duration {
        val newReplicaTime = getAbsoluteTime()
        val delta = if (timeOfLastReplica != null) {
            (newReplicaTime - timeOfLastReplica!!)
        } else {
            ZERO
        }
        timeOfLastReplica = newReplicaTime
        return delta
    }
}
