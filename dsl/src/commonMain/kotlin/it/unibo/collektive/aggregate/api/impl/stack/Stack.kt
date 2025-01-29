package it.unibo.collektive.aggregate.api.impl.stack

import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory

/**
 * Simple Stack interface with additional methods for the aggregate computation.
 */
internal interface Stack {
    /**
     * Returns the current path of the stack.
     */
    fun currentPath(): Path

    /**
     * Pushes the [token] in the stack.
     */
    fun alignRaw(token: Any?)

    /**
     * Pops the last element of the stack.
     */
    fun dealign()

    companion object {
        /**
         * Smart constructor for the [Stack] interface.
         */
        internal operator fun invoke(pathFactory: PathFactory = PathFactory.FullPathFactory): Stack =
            StackDeque(pathFactory)
    }
}
