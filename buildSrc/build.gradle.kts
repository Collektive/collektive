/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

val javaVersion = project.rootDir.resolve(".java-version").readText().trim().substringBefore('.').toInt()

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

with(extensions.getByType<VersionCatalogsExtension>().named("libs")) {
    dependencies {
        implementation(kotlin("reflect"))
        implementation(findLibrary("kotlin-gradle-plugin").get())
        /*
         * The buildSrc classpath is the parent of every project's classloader, so its kotlinx-coroutines
         * (1.8.0 from the Kotlin Gradle Plugin) would shadow the newer one required by other plugins
         * (e.g., publish-on-central), causing NoSuchMethodErrors at runtime.
         */
        runtimeOnly(findLibrary("kotlinx-coroutines-core").get())
    }
}
