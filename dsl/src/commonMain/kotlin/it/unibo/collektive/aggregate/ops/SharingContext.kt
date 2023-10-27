package it.unibo.collektive.aggregate.ops

/**
 * The lambda context passed to the [share] function.
 */
class SharingContext<Initial, Return> {
    internal var toBeSent: Initial? = null
    internal var toBeReturned: Return? = null
    internal var areSameType = true

    /**
     * Express the value [toReturn] after the [share] computation is done.
     * It can be used with checks after the invocation.
     *
     * ## Example
     * ```
     * val res3 = share(0) {
     *   val min = it.min()?.value!!
     *   min butReturn if (min > 1) "Hello" else null
     * }
     * ```
     */
    infix fun Initial.butReturn(toReturn: Return): Return {
        toBeSent = this
        toBeReturned = toReturn
        areSameType = false
        return toReturn
    }
}
