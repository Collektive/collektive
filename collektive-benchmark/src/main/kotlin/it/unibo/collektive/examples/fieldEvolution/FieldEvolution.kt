package it.unibo.collektive.examples.fieldEvolution

import it.unibo.collektive.aggregate.api.Aggregate

/**
 * A simple example of field evolution using the [repeat] function.
 */
fun Aggregate<Int>.fieldEvolution(): Int = repeat(0) { it + 1 }
