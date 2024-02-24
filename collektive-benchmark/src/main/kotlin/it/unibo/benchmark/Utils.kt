package it.unibo.benchmark

/**
 * The specification of a simulation.
 * It contains the [incarnation] used in the simulation, the [testType], the [cycle] in which the simulation is executed
 * and the number of [nodes] in the simulation.
 */
data class SimulationType(val incarnation: String, val testType: String, val cycle: Int, val nodes: Int)

/**
 * The results from a simulation.
 * It contains the [duration] of the simulation and the number of [steps].
 */
data class Results(val duration: Long, val steps: Long)
