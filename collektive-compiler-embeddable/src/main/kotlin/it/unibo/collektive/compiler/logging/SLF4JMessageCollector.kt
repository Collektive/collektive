/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.logging

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A message collector that logs messages using a SLF4J [logger].
 */
class SLF4JMessageCollector(val logger: Logger) : MessageCollector {
    override fun clear() = Unit

    override fun hasErrors(): Boolean = false

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        val logOperation: (String, Array<Any?>) -> Unit =
            when (severity) {
                CompilerMessageSeverity.ERROR, CompilerMessageSeverity.EXCEPTION -> logger::error
                CompilerMessageSeverity.STRONG_WARNING -> logger::warn
                CompilerMessageSeverity.FIXED_WARNING -> logger::warn
                CompilerMessageSeverity.WARNING -> logger::warn
                CompilerMessageSeverity.OUTPUT, CompilerMessageSeverity.INFO -> logger::info
                CompilerMessageSeverity.LOGGING -> logger::debug
            }
        when (location) {
            null -> logOperation("{} {}", arrayOf(severity, message))
            else -> logOperation("{} {} at {}", arrayOf(severity, message, location))
        }
    }

    /**
     * Global defaults.
     */
    companion object {
        /**
         * A default message collector that logs messages to the "Collektive Compiler" SLF4J logger.
         */
        val default = SLF4JMessageCollector(LoggerFactory.getLogger("Collektive Compiler"))
    }
}
