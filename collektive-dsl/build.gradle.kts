/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.id)

configureKotlinMultiplatform()

kotlinMultiplatform {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.arrow.core)
            implementation(libs.hash.sha3)
            implementation(libs.kotlinx.serialization.core)
        }
        commonTest.dependencies {
            implementation(collektive("stdlib"))
            implementation(collektive("test-tooling"))
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.protobuf)
        }
        jvmTest.dependencies {
            implementation(libs.mockk)
        }
    }
}

// The following configuration is redundant since the gradle plugin enables the compiler plugin by default
// It is used to configure the compiler plugin
collektive {
    collektiveEnabled = true
}
