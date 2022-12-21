fun main() {

    aggregate {
        fun test() {
            neighbouring(2)
        }
        fun test2() {
            test()
        }

        test2()
    }
}
