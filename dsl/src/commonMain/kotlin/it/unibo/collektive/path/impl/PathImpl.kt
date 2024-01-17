package it.unibo.collektive.path.impl

import it.unibo.collektive.path.Path

internal data class PathImpl(private val path: List<Any?>) : Path {
    private var hash = 0
    override fun tokens(): List<Any?> = path

    override fun hashCode(): Int {
        if (hash == 0) {
            hash = path.hashCode()
        }
        return hash
    }
}
