package it.unibo.collektive.state

import it.unibo.collektive.stack.Path

/**
 * State of the local node.
 * @param path: path of the state.
 * @param value: value of the state.
 */
data class State<X>(
    val path: Path,
    val value: X?,
)
