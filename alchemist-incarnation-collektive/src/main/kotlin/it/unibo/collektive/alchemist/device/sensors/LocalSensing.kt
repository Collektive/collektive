package it.unibo.collektive.alchemist.device.sensors

/**
 * Distance sensor.
 */
interface LocalSensing {
    /**
     * Sense a molecule given its [name] and returns its value.
     */
    fun <T> sense(name: String): T
}
