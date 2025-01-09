import de.aaschmid.gradle.plugins.cpd.Cpd
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask

plugins {
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotest)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
    id("it.unibo.collektive.collektive-plugin")
}
val reportMerge by tasks.registering(ReportMergeTask::class) {
    output = project.layout.buildDirectory.file("reports/merge.sarif")
}

allprojects {
    group = "it.unibo.${rootProject.name}"

    repositories {
        mavenCentral()
    }

    with(rootProject.libs.plugins) {
        apply(plugin = dokka.id)
        apply(plugin = publishOnCentral.id)
        apply(plugin = kotlin.qa.id)
        apply(plugin = gitSemVer.id)
        apply(plugin = taskTree.id)
        apply(plugin = kover.id)
        apply(plugin = kotest.id)
    }
    apply(plugin = "it.unibo.collektive.collektive-plugin")

    signing {
        if (System.getenv("CI") == "true") {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
    }

    publishOnCentral {
        repoOwner = "Collektive"
        projectLongName = "collektive"
        projectDescription = "Aggregate Computing in Kotlin"
        licenseName = "Apache License 2.0"
        licenseUrl = "https://opensource.org/license/Apache-2.0/"
        publishing {
            publications {
                withType<MavenPublication>().configureEach {
                    pom {
                        developers {
                            developer {
                                name = "Elisa Tronetti"
                                email = "elisa.tronetti@studio.unibo.it"
                                url = "https://github.com/ElisaTronetti"
                            }
                            developer {
                                name = "Danilo Pianini"
                                email = "danilo.pianini@unibo.it"
                                url = "https://danilopianini.org"
                            }
                            developer {
                                name = "Nicolas Farabegoli"
                                email = "nicolas.farabegoli@unibo.it"
                                url = "https://nicolasfarabegoli.it"
                            }
                            developer {
                                name = "Angela Cortecchia"
                                email = "angela.cortecchia@studio.unibo.it"
                                url = "https://github.com/angelacorte"
                            }
                        }
                    }
                }
            }
        }
    }

    plugins.withType<DetektPlugin> {
        val detektTasks = tasks.withType<Detekt>()
            .matching { task ->
                task.name.let { it.endsWith("Main") || it.endsWith("Test") } &&
                    !task.name.contains("Baseline")
            }
        val check by tasks.getting
        val detektAll by tasks.registering {
            group = "verification"
            check.dependsOn(this)
            dependsOn(detektTasks)
        }
    }

    // Enforce the use of the Kotlin version in all subprojects
    configurations.matching { it.name != "detekt" }.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(rootProject.libs.versions.kotlin.get())
            }
        }
    }

    tasks.withType<Detekt>().configureEach { finalizedBy(reportMerge) }
    tasks.withType<GenerateReportsTask>().configureEach { finalizedBy(reportMerge) }
    reportMerge {
        input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
        input.from(tasks.withType<GenerateReportsTask>().flatMap { it.reportsOutputDirectory.asFileTree.files })
    }

    tasks.withType<Cpd>().configureEach {
        exclude {
            it.file.absolutePath.contains("generated", ignoreCase = true)
        }
    }
}

dependencies {
    listOf(
        "dsl",
        "stdlib",
        "alchemist-incarnation-collektive",
    ).forEach {
        kover(project(":$it"))
    }
}

kover {
    reports {
        filters {
            excludes {
                packages("it.unibo.collektive.stdlib.*")
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
