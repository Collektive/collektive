@file:JvmName("ScriptTest")
@file:Suppress("NEIGHBORING_WITH_CONSTANT")
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring

fun Aggregate<Int>.myTest(): Unit = when(localId) {
    in 1..10 -> println(neighboring("foo"))
    else -> println(neighboring("bar"))
}
