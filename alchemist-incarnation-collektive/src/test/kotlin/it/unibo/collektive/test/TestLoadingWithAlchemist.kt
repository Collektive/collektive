package it.unibo.collektive.test

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldNot
import it.unibo.alchemist.boundary.LoadAlchemist
import it.unibo.alchemist.model.terminators.AfterTime
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.util.ClassPathScanner

class TestLoadingWithAlchemist : StringSpec({
    ClassPathScanner.resourcesMatching(".*x\\.ya?ml", "it.unibo.collektive").forEach { simulationFile ->
        val (fileName) = checkNotNull(Regex(".*/([^/]+?)$").matchEntire(simulationFile.path)).destructured
        "test loading and running $fileName" {
            val loader = LoadAlchemist.from(simulationFile)
            loader shouldNot beNull()
            val simulation = loader.getDefault<Any, Nothing>()
            simulation shouldNot beNull()
            simulation.environment.addTerminator(AfterTime(DoubleTime(10.0)))
            loader.launch()
        }
    }
})
