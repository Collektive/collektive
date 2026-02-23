@file:JvmName("ScriptTest")
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring

fun Aggregate<Int>.myTest() {
    val foo = "foo"
    val bar = "bar"
    when (localId) {
        in 1..10 -> println(neighboring(foo))
        else -> println(neighboring(bar))
    }
}
