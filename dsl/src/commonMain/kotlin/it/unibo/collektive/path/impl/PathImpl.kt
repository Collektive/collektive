package it.unibo.collektive.path.impl

import it.unibo.collektive.path.Path

internal data class PathImpl(private val path: List<Any?>) : Path {
    private val hash by lazy { path.hashCode() }
    override fun tokens(): List<Any?> = path

    override fun hashCode(): Int = hash
}
