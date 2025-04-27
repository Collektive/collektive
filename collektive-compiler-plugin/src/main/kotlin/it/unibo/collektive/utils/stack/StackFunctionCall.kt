/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.utils.stack

/**
 * A stack that keeps track of the function calls in the code.
 */
class StackFunctionCall {
    private val stack = ArrayDeque<String>()

    /**
     * Pushes a new function call to the stack.
     */
    fun push(name: String) {
        stack.add(name)
    }

    override fun toString(): String =
        if (stack.isEmpty()) "" else stack.joinToString(prefix = "", separator = "|", postfix = "|")
}
