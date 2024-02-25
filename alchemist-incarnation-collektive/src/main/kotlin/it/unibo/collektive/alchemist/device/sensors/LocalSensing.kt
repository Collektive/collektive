package it.unibo.collektive.alchemist.device.sensors

/**
 * Interface for a device that can sense the environment.
 */
interface LocalSensing {
    /**
     * Sense a molecule given its [name] and returns its value.
     */
    fun <T> sense(name: String): T

    /**
     * Sense a molecule given its [name] and returns its value, or [default] if the molecule is not present.
     */
    fun <T> senseOrElse(name: String, default: T): T
}
