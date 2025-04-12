@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import it.unibo.collektive.collektivize.CollektivizeTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.collektivize)
    alias(libs.plugins.kotlin.power.assert)
}

apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.id)

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
        commonMain {
            dependencies {
                implementation(collektive("dsl"))
                implementation(rootProject.libs.kotlinx.serialization.core)
                implementation(rootProject.libs.arrow.core.serialization)
            }
            kotlin.srcDirs(collektivizeKotlinStdlibTask)
        }
        commonTest.dependencies {
            implementation(collektive("test-tooling"))
            implementation(rootProject.libs.bundles.kotlin.testing.common)
            implementation(rootProject.libs.kotlinx.serialization.json)
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
