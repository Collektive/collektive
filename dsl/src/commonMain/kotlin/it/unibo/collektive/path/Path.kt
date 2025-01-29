package it.unibo.collektive.path

import it.unibo.collektive.path.impl.PathImpl

/**
 * A path represents a specific point in the AST of an aggregate program.
 * The point in the AS is identified as a sequence of tokens.
 */
interface Path {
    /**
     * Returns the path as a sequence of tokens constituting the path.
     */
    fun tokens(): List<Any?>

    /**
     * Path factory.
     */
    companion object {
        /**
         * Creates a path from the given [tokens].
         */
        operator fun invoke(vararg tokens: Any?): Path = PathImpl(tokens.toList())

        /**
         * Creates a path from the given [tokens].
         */
        operator fun invoke(tokens: List<Any?>): Path = PathImpl(tokens)
    }
}
