/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.utils.logging

/**
 * A flag that controls whether debugging actions should be performed.
 *
 * When [PERFORM_ACTIONS] is set to `true`, debug messages are printed to standard output.
 * When set to `false`, debug messages are ignored, and no output is produced.
 *
 * This flag is typically used to toggle verbose logging during the development
 * or testing phases of the Collektive compiler plugin.
 */
private const val PERFORM_ACTIONS = false

/**
 * Conditionally prints a debug message to standard output.
 *
 * If [PERFORM_ACTIONS] is `true`, the [message] is evaluated and printed.
 * Otherwise, the [message] is not evaluated and no action is performed.
 *
 * @param message A lazily evaluated function producing the message to log.
 * @return The debug message if printed, or `null` if suppressed.
 *
 * @see PERFORM_ACTIONS
 */
fun debugPrint(message: () -> String): String? = when {
    PERFORM_ACTIONS -> message().also { println(it) }
    else -> null
}
