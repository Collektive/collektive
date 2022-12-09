fun main() {

    aggregate {
        fun testFunction(value: Int) = neighbouring(value * 2)
        testFunction(3)
    }
}
