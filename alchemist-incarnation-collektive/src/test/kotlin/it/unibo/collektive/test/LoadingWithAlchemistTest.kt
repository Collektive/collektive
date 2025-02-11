package it.unibo.collektive.test

import it.unibo.alchemist.boundary.LoadAlchemist
import it.unibo.alchemist.model.terminators.AfterTime
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.util.ClassPathScanner
import kotlin.test.Test
import kotlin.test.assertNotNull

class LoadingWithAlchemistTest {
    @Test
    fun `test loading and running from resource folder`() {
        ClassPathScanner.resourcesMatching(".*\\.ya?ml", "it.unibo.collektive").forEach { simulationFile ->
            checkNotNull(Regex(".*/([^/]+?)$").matchEntire(simulationFile.path)).destructured
            val loader = LoadAlchemist.from(simulationFile)
            assertNotNull(loader)
            val simulation = loader.getDefault<Any, Nothing>()
            assertNotNull(simulation)
            simulation.environment.addTerminator(AfterTime(DoubleTime(10.0)))
            simulation.play()
            simulation.run()
        }
    }
}
