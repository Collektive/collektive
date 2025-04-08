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
                api(project(":dsl"))
                api(project(":stdlib"))
                implementation("it.unibo.collektive:compiler-plugin")
                implementation(project(":compiler-embeddable"))
                implementation(kotlin("reflect"))
                implementation(libs.bundles.alchemist)
                implementation(libs.bundles.kotlin.compiler)
                implementation(libs.caffeine)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.slf4j)
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
            events =
                setOf(
                    TestLogEvent.FAILED,
                    TestLogEvent.PASSED,
                )
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}
