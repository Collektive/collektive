package it.unibo.collektive

/**
 * A node identifier.
 */
interface ID

/**
 * A node [id] represented by an [Int].
 */
data class IntId(val id: Int) : ID
