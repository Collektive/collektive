package it.unibo.collektive.compiler.logging

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation

/**
 * A message emitted by the Kotlin compiler, with its [severity], the [message] content, and an optional [location].
 */
data class CompilerMessage(
    val severity: CompilerMessageSeverity,
    val message: String,
    val location: CompilerMessageSourceLocation?,
)
