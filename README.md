[![CI/CD](https://github.com/Collektive/collektive/actions/workflows/dispatcher.yml/badge.svg)](https://github.com/Collektive/collektive/actions/workflows/dispatcher.yml)
[![Maven Central](https://img.shields.io/maven-central/v/it.unibo.collektive/collektive-dsl)]()
[![codecov](https://codecov.io/gh/Collektive/collektive/graph/badge.svg?token=U94AUOB5OK)](https://codecov.io/gh/Collektive/collektive)
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventional_commits-e10098?logo=semantic-release)](https://github.com/semantic-release/semantic-release)
![GitHub](https://img.shields.io/github/license/Collektive/collektive)

# Collektive

## Goal

Collektive is a Kotlin multiplatform implementation of [Aggregate Programming](https://cris.unibo.it/bitstream/11585/520779/4/paper-short.pdf),
built from the experience gained with
[Protelis](https://github.com/Protelis/Protelis)
and [Scala Fields (Scafi)](https://github.com/scafi/scafi)

Documentation for Collektive is (will) be available at https://collektive.github.io/

## Idea

Collektive builds on the idea of implementing the
[core mechanisms of the field calculus](https://doi.org/10.1145/3285956),
including [exchange](https://doi.org/10.1016/j.jss.2024.111976),
directly into the Kotlin compiler,
so that aggregate programs can get written in plain Kotlin.

To do so, we use a compiler plugin that changes the behavior of the Kotlin compiler,
annotating the points in code where a program may need to "align",
and "projecting" fields when branching (or boolean short-circuiting operations)
are detected.

## Importing the project

First of all, add the following plugin to your `build.gradle.kts` file:

```kotlin
plugins {
    id("it.unibo.collektive.collektive-plugin") version "<latest version>"
}
```

Then import the domain-specific language and the standard library.

JVM:

```kotlin
dependencies {
    implementation("it.unibo.collektive:collektive-dsl:<latest version>")
    implementation("it.unibo.collektive:collektive-stdlib:<latest version>")
}
```

Multiplatform:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("it.unibo.collektive:collektive-dsl:<latest version>")
                implementation("it.unibo.collektive:collektive-stdlib:<latest version>")
            }
        }
    }
}
```
