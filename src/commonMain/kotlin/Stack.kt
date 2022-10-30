interface Stack {
    fun <X> inNewFrame(frameId: Any, compute: (Path) -> X): X
}

class StackImpl : Stack {
    private val path: Path = PathImpl()
    override fun <X> inNewFrame(frameId: Any, compute: (Path) -> X): X {
        path.addToken(frameId)
        return compute(path)
    }
}
