import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("it.unibo.collektive.kotlinAlignmentPlugin")
}

val os: OperatingSystem = OperatingSystem.current()

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            filter {
                isFailOnNoMatchingTests = false
            }
            testLogging {
                showExceptions = true
                events = setOf(
                    FAILED,
                    PASSED,
                )
                exceptionFormat = FULL
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.kotlin.testing.common)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5.jvm)
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
    }

    js(IR) {
        browser()
        nodejs()
        binaries.library()
    }

    val nativeSetup: KotlinNativeTarget.() -> Unit = {
        compilations["main"].defaultSourceSet.dependsOn(kotlin.sourceSets["nativeMain"])
        compilations["test"].defaultSourceSet.dependsOn(kotlin.sourceSets["nativeTest"])
        binaries {
            sharedLib()
            staticLib()
        }
    }

    linuxX64(nativeSetup)
    linuxArm64(nativeSetup)

    mingwX64(nativeSetup)

    macosX64(nativeSetup)
    macosArm64(nativeSetup)
    ios(nativeSetup)
    watchos(nativeSetup)
    tvos(nativeSetup)

    targets.all {
        compilations.all {
            // enable all warnings as errors
            kotlinOptions {
                allWarningsAsErrors = true
            }
        }
    }

    // Disable cross compilation
    val excludeTargets = when {
        os.isLinux -> kotlin.targets.filterNot { "linux" in it.name }
        os.isWindows -> kotlin.targets.filterNot { "mingw" in it.name }
        os.isMacOsX -> kotlin.targets.filter { "linux" in it.name || "mingw" in it.name }
        else -> emptyList()
    }.mapNotNull { it as? KotlinNativeTarget }

    configure(excludeTargets) {
        compilations.configureEach {
            cinterops.configureEach { tasks[interopProcessingTaskName].enabled = false }
            compileTaskProvider.get().enabled = false
            tasks[processResourcesTaskName].enabled = false
        }
        binaries.configureEach { linkTask.enabled = false }

        mavenPublication {
            tasks.withType<AbstractPublishToMaven>()
                .configureEach { onlyIf { publication != this@mavenPublication } }
            tasks.withType<GenerateModuleMetadata>()
                .configureEach { onlyIf { publication.get() != this@mavenPublication } }
        }
    }
}

kotlinAlignmentPlugin {
    enabled = true
}

afterEvaluate {
    listOf(
        "uploadKotlinOSSRHToMavenCentralNexus",
    ).forEach {
        tasks.named(it).configure {
            enabled = false
        }
    }
}
