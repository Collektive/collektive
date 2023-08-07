import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.danilopianini.gradle.mavencentral.DocStyle

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.detekt)
    alias(libs.plugins.gitSemVer)
}

val Provider<PluginDependency>.id: String get() = get().pluginId

allprojects {
    group = "it.unibo.${rootProject.name}"

    repositories {
        mavenCentral()
    }

    with(rootProject.libs.plugins) {
        apply(plugin = publishOnCentral.id)
        apply(plugin = detekt.id)
        apply(plugin = gitSemVer.id)
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
        docStyle.set(DocStyle.HTML)
        publishing {
            publications {
                withType<MavenPublication>().configureEach {
                    if ("OSSRH" !in name) { artifact(tasks.javadocJar) }
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

    // Enforce the use of the Kotlin version in all subprojects
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(rootProject.libs.versions.kotlin.get())
            }
            if (requested.group == "org.jetbrains.kotlinx" && requested.name == "kotlinx-coroutines-core") {
                useVersion(rootProject.libs.versions.coroutines.get())
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
