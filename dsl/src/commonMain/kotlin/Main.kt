import field.min

fun main() {
    val network: Network = NetworkImpl()

    val f: (Int) -> Int = { it * 2 }
    var i = 0
    val condition: () -> Boolean = { i++ < 2 }

    // Device 1
    aggregate(condition, network) {
        neighbouring(f(3))
    }

    i = 0
    // Device 2
    aggregate(condition, network) {
        println(neighbouring(f(2)).min())
    }
}
