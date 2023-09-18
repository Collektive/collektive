package it.unibo.collektive

import kotlin.random.Random

/**
 * A node identifier.
 */
interface ID

/**
 * A node [id] represented by an [Int].
 */
data class IntId(val id: Int = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)) : ID
