import java.util.Locale

apply(plugin = libs.plugins.kotlin.jvm.id)

plugins{
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.taskTree)
}

multiJvm {
    jvmVersionForCompilation.set(17)
}


kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation(rootProject.libs.alchemist.api)
                implementation(rootProject.libs.alchemist)
                implementation(rootProject.libs.bundles.alchemist)
                implementation(project(":alchemist-incarnation-collektive"))
                implementation(project(":dsl"))
            }
        }
    }
}

val alchemistGroup = "Run Alchemist"

val runAll by tasks.register<DefaultTask>("runAll") {
    group = alchemistGroup
    description = "Launches all simulations"
}

fun String.capitalizeString(): String = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
}

/*
 * Scan the folder with the simulation files, and create a task for each one of them.
 */

println(rootProject.rootDir.path)
File(rootProject.rootDir.path + "/collektive-examples/src/main/yaml").listFiles()
    .orEmpty()
    .apply { check(isNotEmpty()) }
    .filter { it.extension == "yaml" }
    .sortedBy { it.nameWithoutExtension }
    .forEach {
        val task by tasks.register<JavaExec>("run${it.nameWithoutExtension.capitalizeString()}") {
            javaLauncher.set(
                javaToolchains.launcherFor {
                    languageVersion.set(JavaLanguageVersion.of(multiJvm.latestJava))
                }
            )
            group = alchemistGroup
            description = "Launches simulation ${it.nameWithoutExtension}"
            mainClass.set("it.unibo.alchemist.Alchemist")
            classpath = sourceSets["main"].runtimeClasspath
            val exportsDir = File("${projectDir.path}/build/exports/${it.nameWithoutExtension}")
            doFirst {
                if (!exportsDir.exists()) {
                    exportsDir.mkdirs()
                }
            }
            args("run", it.absolutePath)
            args(
                "--override",
                "{ launcher: { parameters: { graphics: \"effects/${it.nameWithoutExtension}.json\" } } }"
            )
            outputs.dir(exportsDir)
        }
        runAll.dependsOn(task)
    }

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}
