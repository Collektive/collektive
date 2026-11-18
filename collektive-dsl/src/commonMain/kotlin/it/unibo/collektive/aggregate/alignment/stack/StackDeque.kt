/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment.stack

import it.unibo.collektive.aggregate.alignment.Path
import it.unibo.collektive.aggregate.alignment.Stack

/**
 * [Stack] that keeps every aligned token in memory, and builds paths from the whole token list
 * through [pathFactory].
 */
class StackDeque(private val pathFactory: PathFactory) : Stack {
    private val currentStack = ArrayDeque<Any?>()

    override fun currentPath(): Path = pathFactory(currentStack.toList())

    override fun alignRaw(token: Any?) = currentStack.addLast(token)

    override fun dealign() {
        currentStack.removeLast()
    }

    override fun toString(): String = currentStack.toString()

    /**
     * Function building a [Path] from the list of currently aligned tokens.
     * The list is a snapshot, and can be retained by the produced [Path].
     */
    typealias PathFactory = (tokens: List<Any?>) -> Path
}
