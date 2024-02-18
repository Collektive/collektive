package it.unibo.alchemist.collektive

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.Position

/**
 * A program to be executed by a [CollektiveDevice], composed of a [name] and a [program] to be executed.
 */
data class CollektiveAlchemistProgram<P : Position<P>>(val name: String, val program: CollektiveDevice<P>.() -> Any?)
