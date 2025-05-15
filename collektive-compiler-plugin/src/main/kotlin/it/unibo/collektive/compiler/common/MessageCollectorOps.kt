/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler.common

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

/**
 * Reports a warning to the message collector.
 */
fun MessageCollector.warn(message: String) = report(CompilerMessageSeverity.WARNING, message)

/**
 * Reports an error to the message collector.
 */
fun MessageCollector.error(message: String) = report(CompilerMessageSeverity.ERROR, message)

/**
 * Reports an informational message to the message collector.
 */
fun MessageCollector.info(message: String) = report(CompilerMessageSeverity.INFO, message)

/**
 * Reports a debug message to the message collector.
 */
fun MessageCollector.debug(message: String) = report(CompilerMessageSeverity.LOGGING, message)

/**
 * Reports a strong warning to the message collector.
 */
fun MessageCollector.strongWarning(message: String) = report(CompilerMessageSeverity.STRONG_WARNING, message)

/**
 * Reports a fatal error to the message collector.
 */
fun MessageCollector.fatal(message: String) = report(CompilerMessageSeverity.EXCEPTION, message)
