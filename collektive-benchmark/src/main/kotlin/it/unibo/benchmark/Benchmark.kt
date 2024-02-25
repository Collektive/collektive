@file:Suppress("MagicNumber")

package it.unibo.benchmark

import it.unibo.alchemist.model.positions.Euclidean2DPosition
import it.unibo.alchemist.model.terminators.AfterTime
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.test.loadYamlSimulation
import it.unibo.alchemist.test.startSimulation
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.SortedMap
import kotlin.io.path.Path

/**
 * The entrypoint of the benchmark running different simulations on different incarnations and test types.
 */
fun main() {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
    val store: MutableMap<SimulationType, Results> = mutableMapOf()
    val incarnations = listOf(
        "scafi",
        "protelis",
        "collektive",
    )
    val tests = listOf(
        "fieldEvolution",
        "neighborCounter",
        "branching",
        "gradient",
        "channelWithObstacles",
    ).flatMap { t -> incarnations.map { i -> i to t } }

    listOf(100, 1000).forEach { simulationTime ->
        val startedAt = LocalDateTime.now().format(formatter)
        repeat(10) { i ->
            tests.map { (incarnation, testType) ->
                val experiment = incarnation to testType
                val simulation = loadYamlSimulation<Any?, Euclidean2DPosition>("yaml/$incarnation/$testType.yml")
                simulation.environment.addTerminator(AfterTime(DoubleTime(simulationTime.toDouble())))
                Thread.sleep(1000)

                val startTime = System.currentTimeMillis()
                simulation.startSimulation(steps = Long.MAX_VALUE)
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime
                println("Simulation $experiment took $duration ms")
                store[SimulationType(experiment.first, experiment.second, i, simulation.environment.nodes.size)] =
                    Results(duration, simulation.step)
            }
        }
        val finishedAt = LocalDateTime.now().format(formatter)
        val sortedStore = store.toSortedMap(
            compareBy<SimulationType> { it.incarnation }
                .thenBy { it.testType }
                .thenBy { it.nodes }
                .thenBy { it.cycle },
        )
        val averageStore = store.entries.groupBy { it.key.incarnation to it.key.testType }.mapValues { (_, res) ->
            (res.sumOf { it.value.duration } / res.size).toDouble()
        }
        generateFiles(sortedStore, averageStore, simulationTime.toDouble(), startedAt, finishedAt)
    }
}

private fun generateFiles(
    sortedMap: SortedMap<SimulationType, Results>,
    average: Map<Pair<String, String>, Double>,
    simulationTime: Double,
    startedAt: String,
    finishedAt: String,
) {
    val path = File("${Path("").toAbsolutePath()}/results")
    if (!path.exists()) path.mkdir()
    sortedMap.toTxt(
        Paths.get(path.toString(), "results$finishedAt.txt"),
        average,
        simulationTime,
        startedAt,
        finishedAt,
    )
    average.toCSV(Paths.get(path.toString(), "results$finishedAt.csv"), simulationTime)
}

private fun SortedMap<SimulationType, Results>.toTxt(
    path: Path,
    average: Map<Pair<String, String>, Double>,
    simulationTime: Double,
    startedAt: String,
    finishedAt: String,
) {
    val file = File(path.toString())
    println(path.toString())
    if (!file.exists()) file.createNewFile()
    Files.write(
        Paths.get(path.toString()),
        (
            "Test started at: $startedAt - Finished at $finishedAt\n" +
                "Results for simulation time $simulationTime s:${this.map { "\n$it" }}\n" +
                "Average:${average.map { "\n$it" }}\n"
            ).toByteArray(),
        if (file.exists()) APPEND else CREATE,
    )
}

private fun Map<Pair<String, String>, Double>.toCSV(path: Path, terminationTime: Double) {
    val file = File(path.toString())
    val writer = BufferedWriter(FileWriter(file, true))

    if (!file.exists()) writer.write("Incarnation,TestType,Average,Simulation Time\n")
    this.forEach { (key, entry) ->
        writer.write("${key.first},${key.second},$entry,$terminationTime\n")
    }
    writer.close()
}
