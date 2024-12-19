package it.unibo.collektive.state.impl

import it.unibo.collektive.path.Path
import it.unibo.collektive.state.State

@Suppress("UNCHECKED_CAST")
internal fun <T> State.getTyped(
    path: Path,
    default: T,
): T = (this as Map<Path, T>).getOrElse(path) { default }
