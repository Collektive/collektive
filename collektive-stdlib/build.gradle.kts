/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

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
//            languageSettings.enableLanguageFeature("ContextParameters")
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

// tasks.withType<KotlinCompilationTask<*>>().configureEach {
//    compilerOptions {
//        freeCompilerArgs.add("-Xcontext-parameters")
//    }
// }
