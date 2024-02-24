import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.util.Locale

apply(plugin = libs.plugins.kotlin.jvm.id)
apply<ScalaPlugin>()

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation(project(":alchemist-incarnation-collektive"))
                implementation(kotlin("reflect"))
                implementation(libs.bundles.protelis)
                implementation(libs.bundles.alchemist)
                implementation(libs.caffeine)
                implementation(libs.scala)
            }
        }
        val test by getting {
            dependencies {
                implementation(rootProject.libs.kotest.runner.junit5.jvm)
            }
        }
    }
}

// Heap size estimation for batches
val maxHeap: Long? by project
val heap: Long = maxHeap ?: if (System.getProperty("os.name").lowercase().contains("linux")) {
    ByteArrayOutputStream().use { output ->
        exec {
            executable = "bash"
            args = listOf("-c", "cat /proc/meminfo | grep MemAvailable | grep -o '[0-9]*'")
            standardOutput = output
        }
        output.toString().trim().toLong() / 1024
    }.also { println("Detected ${it}MB RAM available.") } * 9 / 10
} else {
    // Guess 16GB RAM of which 2 used by the OS
    14 * 1024L
}
val taskSizeFromProject: Int? by project
val taskSize = taskSizeFromProject ?: 512
val batchGroup = "Run Batch"
val alchemistGroup = "Run Alchemist"
val threadCount = maxOf(1, minOf(Runtime.getRuntime().availableProcessors(), heap.toInt() / taskSize))
val runAllBatch by tasks.register<DefaultTask>("runAllBatch") {
    group = batchGroup
    description = "Launches all experiments"
}
val runAllGraphic by tasks.register<DefaultTask>("runAllGraphic") {
    group = alchemistGroup
    description = "Launches all simulations with the graphic subsystem enabled"
}

fun String.capitalizeString(): String =
    this.replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase(
                Locale.getDefault(),
            )
        } else {
            it.toString()
        }
    }

val incarnations = listOf("collektive", "protelis", "scafi")
incarnations.forEach { incarnation ->
    File(rootProject.rootDir.path + "/src/main/resources/yaml/$incarnation").listFiles()
        ?.filter { it.extension == "yml" }
        ?.sortedBy { it.nameWithoutExtension }
        ?.forEach {
            fun basetask(
                name: String,
                additionalConfiguration: JavaExec.() -> Unit = {},
            ) = tasks.register<JavaExec>(name) {
                description = "Launches graphic simulation ${it.nameWithoutExtension} with Collektive incarnation"
                mainClass.set("it.unibo.alchemist.Alchemist")
//                classpath = sourceSets["main"].runtimeClasspath
                args("run", it.absolutePath)
                if (System.getenv("CI") == "true") {
                    args("--override", "terminate: { type: AfterTime, parameters: [2] } ")
                } else {
                    this.additionalConfiguration()
                }
            }
            val capitalizedName = (incarnation + it.nameWithoutExtension.capitalizeString()).capitalizeString()
            val graphic by basetask("run${capitalizedName}Graphic") {
                group = alchemistGroup
                args(
                    "--override",
                    "monitors: { type: SwingGUI, parameters: { graphics: effects/${it.nameWithoutExtension}.json } }",
                    "--override",
                    "launcher: { parameters: { batch: [], autoStart: false } }",
                )
            }
            runAllGraphic.dependsOn(graphic)
            val batch by basetask("run$capitalizedName") {
                group = batchGroup
                description = "Launches batch experiments for $capitalizedName"
                maxHeapSize = "${minOf(heap.toInt(), Runtime.getRuntime().availableProcessors() * taskSize)}m"
                File("data").mkdirs()
            }
            runAllBatch.dependsOn(batch)
        }
}

tasks.withType(KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

tasks.register<JavaExec>("runBenchmark") {
    group = "Run Benchmark"
    description = "Launches benchmarks for Collektive, ScaFi and Protelis"
    mainClass.set("it.unibo.benchmark.BenchmarkKt")
//    classpath = sourceSets["main"].runtimeClasspath
}
