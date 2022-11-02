fun main() {
    val network: Network = NetworkImpl()

    val f: (Int) -> Int = { it * 2 }
    var i = 0
    val condition: () -> Boolean = { i++ < 2 }

    // Device 1
    aggregate(condition, network) {
        repeating(1, f)
        neighbouring("hello1")
    }

    i = 0
    // Device 2
    aggregate(condition, network) {
        repeating(1, f)
        println(neighbouring("hello2"))
    }
}
