/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

import de.aaschmid.gradle.plugins.cpd.Cpd
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.build.config)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
}

group = "it.unibo.collektive"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.serialization.core)
}

buildConfig {
    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"$group.collektive-compiler-plugin\"")
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

tasks.generateBuildConfig.configure {
    mustRunAfter(tasks.cpdKotlinCheck)
}

ktlint {
    filter {
        exclude {
            it.file.path.contains(
                layout.buildDirectory
                    .dir("generated")
                    .get()
                    .toString(),
            )
        }
    }
}

signing {
    if (System.getenv("CI") == "true") {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

publishOnCentral {
    val githubSlug = "Collektive/collektive"
    projectUrl = "https://github.com/$githubSlug"
    projectDescription = "A Kotlin compiler plugin performing transparent aggregate alignment"
    licenseName = "Apache License 2.0"
    projectLongName = "Collektive kotlin compiler plugin"
    licenseUrl = "https://opensource.org/license/Apache-2.0/"
    publishing {
        publications {
            withType<MavenPublication>().configureEach {
                scmConnection = "git:git@github.com:$githubSlug"
                projectUrl = "https://github.com/$githubSlug"
                pom {
                    developers {
                        developer {
                            name = "Elisa Tronetti"
                            email = "elisa.tronetti@studio.unibo.it"
                            url = "https://github.com/ElisaTronetti"
                        }
                        developer {
                            name = "Nicolas Farabegoli"
                            email = "nicolas.farabegoli@unibo.it"
                            url = "https://nicolasfarabegoli.it"
                        }
                        developer {
                            name = "Danilo Pianini"
                            email = "danilo.pianin@unibo.it"
                            url = "https://danysk.github.io"
                        }
                    }
                }
            }
        }
    }
}

val dslSource = rootProject.layout.buildDirectory.dir("dsl-api")

sourceSets {
    main {
        kotlin {
            srcDir(dslSource)
        }
    }
}

val importDsl by tasks.registering(Copy::class) {
    listOf(
        "Aggregate",
        "DataSharingMethod",
        "Field",
        "FieldEntry",
        "CollektiveIgnore",
        "YieldSupport",
    ).forEach { file ->
        from(
            rootProject.rootDir.resolve("../collektive-dsl/src/commonMain/kotlin/").walkTopDown().single {
                it.name == "$file.kt"
            },
        )
    }
    into(dslSource)
}

inline fun <reified T : Task> dependOnDslSource() {
    tasks.withType<T>().configureEach {
        dependsOn(importDsl)
    }
}

tasks.withType<Detekt>().configureEach {
    setSource("src/main/kotlin")
}

tasks.detektTest.configure {
    // Tests are performed in a dedicated project
    enabled = false
}

dependOnDslSource<Cpd>()
dependOnDslSource<DokkaGenerateTask>()
dependOnDslSource<KotlinCompilationTask<*>>()
dependOnDslSource<KtLintFormatTask>()
dependOnDslSource<KtLintCheckTask>()
dependOnDslSource<Jar>()
dependOnDslSource<org.gradle.jvm.tasks.Jar>()
