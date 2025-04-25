/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    includeBuild("collektive-gradle-plugin")
}

plugins {
    id("com.gradle.develocity") version "4.0.1"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.22"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        uploadInBackground = !System.getenv("CI").toBoolean()
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    preCommit {
        tasks("detektAll", "ktlintCheck")
    }
    createHooks(overwriteExisting = true)
}

rootProject.name = "collektive"

includeBuild("collektive-compiler-plugin")
includeBuild("collektivize")
include(
    "alchemist-incarnation-collektive",
    "collektive-compiler-embeddable",
    "collektive-compiler-plugin-test",
    "collektive-dsl",
    "collektive-stdlib",
    "collektive-test-tooling"
)
