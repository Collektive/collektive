object Aggregate {
    @JvmStatic
    fun entrypoint() = aggregate {
        neighbouring("test")
    }
}