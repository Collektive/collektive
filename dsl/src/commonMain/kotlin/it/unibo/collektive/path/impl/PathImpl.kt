package it.unibo.collektive.path.impl

import it.unibo.collektive.path.Path

internal data class PathImpl(private val path: List<Any?>) : Path {
    override fun tokens(): List<Any?> = path
}
