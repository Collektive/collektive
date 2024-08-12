@file:JvmName("TestAggregateInLoop")
import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.exampleAggregate(): Unit =
    println()

fun Aggregate<Int>.x() {
    val x = 10
    for (j in listOf(1, 2, 3)) {
        this.exampleAggregate() // Warning: aggregate function called inside a loop with no manual alignment operation
    }
}
