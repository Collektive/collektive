/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

plugins {
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlinx.benchmark)
}

apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.id)

kotlinMultiplatform {
    jvm()
    js {
        nodejs()
    }
    sourceSets {
        commonMain.dependencies {
            implementation(collektive("dsl"))
            implementation(collektive("stdlib"))
            implementation(collektive("test-tooling"))
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}

collektive {
    collektiveEnabled = true
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("jvm")
        register("js")
    }
    configurations {
        named("main") {
            warmups = 3
            iterations = 5
            iterationTime = 1
            iterationTimeUnit = "s"
        }
        // Compares the candidate full-path stacks only
        register("fullPath") {
            warmups = 3
            iterations = 5
            iterationTime = 1
            iterationTimeUnit = "s"
            param(
                "profile",
                "ForSimulation",
                "ForSimulationConsList",
                "ForSimulationConsListFnv1a32",
                "ForSimulationArrayList",
                "ForSimulationArrayListFnv1a32",
            )
        }
        // Quick run to check that the benchmarks work, not to measure anything
        register("smoke") {
            warmups = 1
            iterations = 1
            iterationTime = 100
            iterationTimeUnit = "ms"
        }
    }
}

// Benchmarks are not a deliverable
tasks.withType<AbstractPublishToMaven>().configureEach { enabled = false }
