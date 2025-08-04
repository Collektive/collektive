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
    java
}

apply(plugin = libs.plugins.kotlin.jvm.id)

val targetJvm = JvmTarget.JVM_1_8

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJvm.target.substringAfterLast('.').toInt()))
    }
}

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation("it.unibo.collektive:collektive-compiler-plugin:${project.version}")
                implementation(libs.bundles.kotlin.compiler)
                implementation(libs.slf4j)
            }
        }
        val test by getting {
            dependencies {
                implementation(collektive("compiler-embeddable"))
                implementation(collektive("dsl"))
                implementation(collektive("stdlib"))
                implementation(kotlin("test"))
                implementation(rootProject.libs.kotest.runner.junit5.jvm)
                implementation(libs.javap)
                implementation(libs.kotlinpoet)
                implementation(libs.subjekt)
            }
        }
    }
    compilerOptions {
        jvmTarget.set(targetJvm)
        freeCompilerArgs.add("-Xcontext-parameters")
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

tasks {
    // Prevent publishing this module since it is a test-only module
    withType<AbstractPublishToMaven>().configureEach {
        enabled = false
    }
    withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }
}
