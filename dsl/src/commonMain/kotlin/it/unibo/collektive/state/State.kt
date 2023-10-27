package it.unibo.collektive.state

import it.unibo.collektive.stack.Path

/**
 * State composed of [path] and [value] of a node.
 */
data class State<X>(
    val path: Path,
    val value: X?,
)
