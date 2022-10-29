interface Stack {
    fun <X> inNewFrame(frameId: Any, compute: (Path) -> X): X = compute(Path())
}