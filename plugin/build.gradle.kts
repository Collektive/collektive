import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.danilopianini.gradle.mavencentral.DocStyle
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
    alias(libs.plugins.kotlin.qa)
}

val Provider<PluginDependency>.id: String get() = get().pluginId

val os: OperatingSystem = OperatingSystem.current()

allprojects {
    group = "it.unibo.collektive"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    with(rootProject.libs.plugins) {
        apply(plugin = kotlin.jvm.id)
        apply(plugin = build.config.id)
        apply(plugin = gitSemVer.id)
        apply(plugin = publishOnCentral.id)
        apply(plugin = taskTree.id)
        apply(plugin = kotlin.qa.id)
    }

    signing {
        if (System.getenv("CI") == "true") {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
    }

    publishOnCentral {
        projectUrl = "https://github.com/Collektive/${rootProject.name}"
        projectLongName = "collektive"
        projectDescription = "DSL for Aggregate Computing in Kotlin"
        licenseName = "Apache License 2.0"
        licenseUrl = "https://opensource.org/license/Apache-2.0/"
        docStyle = DocStyle.HTML
        publishing {
            publications {
                // Upload these artifacts only from Linux to prevent overlapping with other OS in CI.
                tasks.withType<AbstractPublishToMaven>().configureEach {
                    onlyIf { os.isLinux }
                }
                withType<MavenPublication>().configureEach {
                    if ("OSSRH" !in name) {
                        artifact(tasks.javadocJar)
                    }
                    scmConnection = "git:git@github.com:Collektive/${rootProject.name}"
                    projectUrl = "https://github.com/Collektive/${rootProject.name}"
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
                        }
                    }
                }
            }
        }
    }

    plugins.withType<DetektPlugin> {
        val check by tasks.getting
        val detektAll by tasks.creating { group = "verification" }
        tasks.withType<Detekt>()
            .matching { task ->
                task.name.let { it.endsWith("Main") || it.endsWith("Test") } && !task.name.contains("Baseline")
            }
            .all {
                check.dependsOn(this)
                detektAll.dependsOn(this)
            }
    }

    tasks.withType<KotlinCompile<*>>().configureEach {
        kotlinOptions {
            freeCompilerArgs += listOf("-Xcontext-receivers")
        }
    }
}

tasks {
    // Prevent publishing the root project (since is empty)
    withType<AbstractPublishToMaven>().configureEach {
        enabled = false
    }
    withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }
}
