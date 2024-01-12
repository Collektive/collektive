package it.unibo.collektive.alchemist.utils

import it.unibo.alchemist.model.Node
import it.unibo.collektive.ID
import it.unibo.collektive.IntId

/**
 * Convert a node to an ID.
 */
fun <T> Node<T>.toId(): ID = IntId(this.id)
