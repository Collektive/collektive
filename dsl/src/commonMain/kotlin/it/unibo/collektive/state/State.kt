package it.unibo.collektive.state

import it.unibo.collektive.stack.Path

/**
 * State composed of [path] and [value] of a node.
 */
typealias State = Map<Path, Any?>

@Suppress("UNCHECKED_CAST")
internal fun <T> State.getTyped(path: Path, default: T): T = (this as Map<Path, T>).getOrElse(path) { default }
