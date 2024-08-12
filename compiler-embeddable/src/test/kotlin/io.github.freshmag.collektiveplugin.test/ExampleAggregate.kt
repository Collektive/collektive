package io.github.freshmag.collektiveplugin.test

import it.unibo.collektive.aggregate.api.Aggregate


fun Aggregate<Int>.exampleAggregate(): Unit =
    println()

fun Aggregate<Int>.x() {
    for (j in listOf(1, 2, 3)) {
        exampleAggregate() // Warning: aggregate function called inside a loop with no manual alignment operation
    }
}
