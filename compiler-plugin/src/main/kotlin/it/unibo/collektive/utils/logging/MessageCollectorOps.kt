package it.unibo.collektive.utils.logging

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

/**
 * Report a warning to the message collector.
 */
fun MessageCollector.warn(message: String) = report(CompilerMessageSeverity.WARNING, message)

/**
 * Report an error to the message collector.
 */
fun MessageCollector.error(message: String) = report(CompilerMessageSeverity.ERROR, message)

/**
 * Report an info to the message collector.
 */
fun MessageCollector.info(message: String) = report(CompilerMessageSeverity.INFO, message)

/**
 * Report a debug message to the message collector.
 */
fun MessageCollector.debug(message: String) = report(CompilerMessageSeverity.LOGGING, message)

/**
 * Report a strong warning to the message collector.
 */
fun MessageCollector.strongWarning(message: String) = report(CompilerMessageSeverity.STRONG_WARNING, message)

/**
 * Report a fatal error to the message collector.
 */
fun MessageCollector.fatal(message: String) = report(CompilerMessageSeverity.EXCEPTION, message)
