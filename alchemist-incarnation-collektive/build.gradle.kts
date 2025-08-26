/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
}

apply(plugin = rootProject.libs.plugins.kotlin.jvm.id)

val targetJvm = JvmTarget.JVM_17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJvm.target.toInt()))
    }
}

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                api(project(":collektive-dsl"))
                api(project(":collektive-stdlib"))
                implementation("it.unibo.collektive:collektive-compiler-plugin:${project.version}")
                implementation(project(":collektive-compiler-embeddable"))
                implementation(kotlin("reflect"))
                implementation(libs.bundles.alchemist)
                implementation(libs.bundles.kotlin.compiler)
                implementation(libs.caffeine)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.slf4j)
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
    compilerOptions {
        jvmTarget.set(targetJvm)
    }
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            events = TestLogEvent.entries.toSet()
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}
