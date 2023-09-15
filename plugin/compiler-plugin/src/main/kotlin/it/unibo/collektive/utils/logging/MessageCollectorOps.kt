package it.unibo.collektive.utils.logging

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

fun MessageCollector.warn(message: String) = report(CompilerMessageSeverity.WARNING, message)
fun MessageCollector.error(message: String) = report(CompilerMessageSeverity.ERROR, message)
fun MessageCollector.info(message: String) = report(CompilerMessageSeverity.INFO, message)
fun MessageCollector.debug(message: String) = report(CompilerMessageSeverity.LOGGING, message)
fun MessageCollector.trace(message: String) = report(CompilerMessageSeverity.STRONG_WARNING, message)
fun MessageCollector.fatal(message: String) = report(CompilerMessageSeverity.EXCEPTION, message)
fun MessageCollector.internalError(message: String) = report(CompilerMessageSeverity.EXCEPTION, message)
