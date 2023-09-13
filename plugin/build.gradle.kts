import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
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
    }

    signing {
        if (System.getenv("CI") == "true") {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
    }

    publishOnCentral {
        projectUrl.set("https://github.com/Collektive/${rootProject.name}")
        projectLongName.set("collektive")
        projectDescription.set("DSL for Aggregate Computing in Kotlin")
        licenseName.set("MIT License")
        licenseUrl.set("https://opensource.org/license/mit/")
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
                    scmConnection.set("git:git@github.com:Collektive/${rootProject.name}")
                    projectUrl.set("https://github.com/Collektive/${rootProject.name}")
                    pom {
                        developers {
                            developer {
                                name.set("Elisa Tronetti")
                                email.set("elisa.tronetti@studio.unibo.it")
                                url.set("https://github.com/ElisaTronetti")
                            }
                        }
                    }
                }
            }
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
