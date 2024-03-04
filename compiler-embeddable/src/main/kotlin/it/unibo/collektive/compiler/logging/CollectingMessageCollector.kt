package it.unibo.collektive.compiler.logging

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.EXCEPTION
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

/**
 * A message collector collecting messages for later programmatic use.
 */
class CollectingMessageCollector : MessageCollector {

    private val timeline: MutableList<CompilerMessage> = mutableListOf()

    /**
     * All messages collected so far, sorted by generation time.
     */
    val messages: List<CompilerMessage> get() = timeline

    /**
     * All messages collected so far, grouped by severity.
     */
    fun allMessages(): Map<CompilerMessageSeverity, List<CompilerMessage>> = messages.groupBy { it.severity }

    /**
     * All messages collected so far with the given [severity].
     */
    operator fun get(severity: CompilerMessageSeverity): List<CompilerMessage> =
        messages.filter { it.severity == severity }

    override fun clear() = timeline.clear()

    override fun hasErrors(): Boolean = listOf(ERROR, EXCEPTION).let { errors ->
        messages.any { it.severity in errors }
    }

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ) {
        timeline += CompilerMessage(severity, message, location)
    }
}
