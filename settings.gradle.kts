pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    includeBuild("gradle-plugin")
}

plugins {
    id("com.gradle.develocity") version "3.18"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.9"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
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

includeBuild("compiler-plugin")
include("alchemist-incarnation-collektive", "compiler-embeddable", "compiler-plugin-test", "dsl", "stdlib")
