package it.unibo.collektive.path.impl

import it.unibo.collektive.path.Path

internal data class PathImpl(private val path: List<Any?>) : Path {

    private val hash = path.hashCode()

    override fun tokens(): List<Any?> = path

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Path -> false
        else -> hash == other.hashCode() && path == other.tokens()
    }

    companion object {
        private val cache = mutableMapOf<List<Any?>, Path>()
        private const val MAX_CACHE_SIZE = 10_000
        private const val CACHE_CLEANUP_SIZE = 1000

        fun of(path: List<Any?>): Path = cache.getOrPut(path) { PathImpl(path) }.also {
            if (cache.size >= MAX_CACHE_SIZE) {
                val iterator = cache.iterator()
                repeat(CACHE_CLEANUP_SIZE) {
                    iterator.next()
                    iterator.remove()
                }
            }
        }
    }
}
