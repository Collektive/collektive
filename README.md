[![CI/CD](https://github.com/Collektive/collektive/actions/workflows/dispatcher.yml/badge.svg)](https://github.com/Collektive/collektive/actions/workflows/dispatcher.yml)
[![Maven Central](https://img.shields.io/maven-central/v/it.unibo.collektive/dsl)]()
[![codecov](https://codecov.io/gh/Collektive/collektive/graph/badge.svg?token=U94AUOB5OK)](https://codecov.io/gh/Collektive/collektive)
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventional_commits-e10098?logo=semantic-release)](https://github.com/semantic-release/semantic-release)
![GitHub](https://img.shields.io/github/license/Collektive/collektive)

# Collektive

## Goal

The main goal of this project is to provide a DSL that allows
take easily advantage of aggregate computing, which is a paradigm
that takes full opportunity of the high availability of computational
devices by the means of computational fields.

Using Kotlin Multiplatform allows the DSL to be used in Native, JS and
JVM environments.

## Usage

First of all, add the following plugin to your `build.gradle.kts` file:

```kotlin
plugins {
    id("it.unibo.collektive.collektive-plugin") version "<latest version>"
}
```

This plugin is used to apply the Kotlin compiler plugin used for the alignment of aggregate operator.
Then, add the dependency to the project:
    
```kotlin
dependencies {
    implementation("it.unibo.collektive:collektive-dsl:<latest version>")
}
```

## Project structure

Currently, the project is composed by three submodules:

- **plugin**: it is divided in:
    - **compiler-plugin**: the compiler plugin is used modify a data
      structure used to keep track of the stack at runtime. For each function,
      when its called, its name is registered in the data structure;
    - **gradle-plugin**: necessary plugin that a gradle project uses in
      order to include the compiler plugin;
- **dsl**: the actual DSL, where the logic is implemented and that
  exposes the operator of the aggregate computing;
- **collektive-test**: this folder contains a test that allows to calculate
  the gradient between nodes using the simulation environment offered by
  [Alchemist Simulator](https://alchemistsimulator.github.io/).
