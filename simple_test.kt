package it.unibo.collektive.test

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring

fun testNeighboring(): Aggregate<Int>.() -> Unit = {
    val field = neighboring(42)
}