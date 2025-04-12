@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import it.unibo.collektive.collektivize.CollektivizeTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.collektivize)
    alias(libs.plugins.kotlin.power.assert)
}

apply(plugin = libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

collektive {
    collektiveEnabled = true
}

collektivize {
    outputDirectory =
        layout.buildDirectory
            .dir("generated/kotlin/collektive")
            .get()
            .asFile
}

val collektivizeKotlinStdlibTask = tasks.named<CollektivizeTask>("collektivizeKotlinStdlib")

// Avoid verification tasks to complain about being not dependent on the code generation tasks
tasks.withType<SourceTask>().configureEach {
    if (this is VerificationTask) {
        dependsOn(collektivizeKotlinStdlibTask)
    }
}

kotlinMultiplatform {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
            kotlin.srcDirs(collektivizeKotlinStdlibTask)
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":test-tooling"))
                implementation(rootProject.libs.bundles.kotlin.testing.common)
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(rootProject.libs.kotest.runner.junit5.jvm)
            }
        }
    }
}

powerAssert {
    functions =
        listOf(
            "assert",
            "check",
            "checkNotNull",
            "require",
            "requireNotNull",
            "test.assertTrue",
            "test.assertEquals",
            "test.assertNull",
        ).map { "kotlin.$it" }
    includedSourceSets = listOf("commonMain", "commonTest")
}
