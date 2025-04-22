import de.aaschmid.gradle.plugins.cpd.Cpd
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask

/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
plugins {
    `java-gradle-plugin`
    alias(libs.plugins.build.config)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.gradlePluginPublish)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
}

group = "it.unibo.collektive"
val githubSlug = "collektive/collektive"

class ProjectInfo {
    val longName = "Collektivize: Collektive Code Generator"
    val description = "This plugin generates a \"fielded\" version of any Kotlin library."
    val website = "https://collektive.github.io/"
    val vcsUrl = "https://github.com/$githubSlug.git"
    val scm = "scm:git:github.com/$githubSlug.git"
    val pluginImplementationClass = "$group.collektivize.CollektivizeGradlePlugin"
    val tags = listOf("codegen", "collektive", "kotlin")
}
val info = ProjectInfo()

repositories {
    mavenCentral()
}

val dslSource = rootProject.layout.buildDirectory.dir("field-code")

sourceSets {
    main {
        kotlin {
            srcDir(dslSource)
        }
    }
}

// check(gradle.includedBuilds.size == 1) {
//    "This build is designed for a single inclusion, not ${gradle.includedBuilds.size}. Fix it."
// }

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(rootProject.libs.kotlinpoet)
    implementation(rootProject.libs.slf4j)
}

signing {
    if (System.getenv("CI") == "true") {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        showCauses = true
        showStackTraces = true
        events(
            *org.gradle.api.tasks.testing.logging.TestLogEvent
                .values(),
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

gradlePlugin {
    plugins {
        website = info.website
        vcsUrl = info.vcsUrl
        create("") {
            id = "$group.${project.name}"
            displayName = info.longName
            description = project.description
            implementationClass = info.pluginImplementationClass
            tags = info.tags
        }
    }
}

publishOnCentral {
    projectLongName = info.longName
    projectDescription = info.description
    projectUrl = info.website
    scmConnection = info.scm
    publishing {
        publications {
            withType<MavenPublication>().configureEach {
                pom {
                    developers {
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

val importDsl by tasks.registering(Copy::class) {
    listOf("Field", "FieldEntry").forEach { file ->
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

dependOnDslSource<Cpd>()
dependOnDslSource<DokkaGenerateTask>()
dependOnDslSource<KotlinCompilationTask<*>>()
dependOnDslSource<KtLintFormatTask>()
dependOnDslSource<KtLintCheckTask>()
dependOnDslSource<Jar>()
dependOnDslSource<org.gradle.jvm.tasks.Jar>()
