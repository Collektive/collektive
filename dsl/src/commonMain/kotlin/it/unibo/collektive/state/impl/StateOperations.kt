package it.unibo.collektive.state.impl

import it.unibo.collektive.path.PathSummary
import it.unibo.collektive.state.State

@Suppress("UNCHECKED_CAST")
internal fun <T> State.getTyped(path: PathSummary, default: T): T =
    (this as Map<PathSummary, T>).getOrElse(path) { default }
