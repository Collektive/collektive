@file:JvmName("ScriptTest")
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.*
fun Aggregate<Int>.myTest(): Unit = when(localId) {
    in 1..10 -> println(neighboringViaExchange("foo"))
    else -> println(neighboringViaExchange("bar"))
}
