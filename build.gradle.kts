import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.danilopianini.gradle.mavencentral.DocStyle
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.taskTree)
    alias(libs.plugins.kover)
    id("it.unibo.collektive.collektive-plugin")
}

val Provider<PluginDependency>.id: String get() = get().pluginId

val reportMerge by tasks.registering(ReportMergeTask::class) {
    output = project.layout.buildDirectory.file("reports/detekt/merge.sarif")
}

val os: OperatingSystem = OperatingSystem.current()

allprojects {
    group = "it.unibo.${rootProject.name}"

    repositories {
        mavenCentral()
    }

    with(rootProject.libs.plugins) {
        apply(plugin = kotlin.multiplatform.id)
        apply(plugin = dokka.id)
        apply(plugin = publishOnCentral.id)
        apply(plugin = kotlin.qa.id)
        apply(plugin = gitSemVer.id)
        apply(plugin = taskTree.id)
        apply(plugin = kover.id)
    }
    apply(plugin = "it.unibo.collektive.collektive-plugin")

    kotlin {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
                filter {
                    isFailOnNoMatchingTests = false
                }
                testLogging {
                    showExceptions = true
                    events = setOf(
                        TestLogEvent.FAILED,
                        TestLogEvent.PASSED,
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                }
            }
        }

        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(rootProject.libs.kotlinx.coroutines)
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(rootProject.libs.bundles.kotlin.testing.common)
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(rootProject.libs.kotest.runner.junit5.jvm)
                }
            }
            val nativeMain by creating {
                dependsOn(commonMain)
            }
            val nativeTest by creating {
                dependsOn(commonTest)
            }

            all {
                languageSettings {
                    languageVersion = "2.0"
                }
            }
        }

        js(IR) {
            browser()
            nodejs()
            binaries.library()
        }

        val nativeSetup: KotlinNativeTarget.() -> Unit = {
            compilations["main"].defaultSourceSet.dependsOn(kotlin.sourceSets["nativeMain"])
            compilations["test"].defaultSourceSet.dependsOn(kotlin.sourceSets["nativeTest"])
            binaries {
                sharedLib()
                staticLib()
            }
        }

        applyDefaultHierarchyTemplate()

        linuxX64(nativeSetup)
        linuxArm64(nativeSetup)

        mingwX64(nativeSetup)

        macosX64(nativeSetup)
        macosArm64(nativeSetup)
        iosArm64(nativeSetup)
        iosX64(nativeSetup)
        iosSimulatorArm64(nativeSetup)
        watchosArm64(nativeSetup)
        watchosX64(nativeSetup)
        watchosSimulatorArm64(nativeSetup)
        tvosArm64(nativeSetup)
        tvosX64(nativeSetup)
        tvosSimulatorArm64(nativeSetup)

        targets.all {
            compilations.all {
                // enable all warnings as errors
                kotlinOptions {
//                    allWarningsAsErrors = true
                }
            }
        }

        // Disable cross compilation
        val excludeTargets = when {
            os.isLinux -> kotlin.targets.filterNot { "linux" in it.name }
            os.isWindows -> kotlin.targets.filterNot { "mingw" in it.name }
            os.isMacOsX -> kotlin.targets.filter { "linux" in it.name || "mingw" in it.name }
            else -> emptyList()
        }.mapNotNull { it as? KotlinNativeTarget }

        configure(excludeTargets) {
            compilations.configureEach {
                cinterops.configureEach { tasks[interopProcessingTaskName].enabled = false }
                compileTaskProvider.get().enabled = false
                tasks[processResourcesTaskName].enabled = false
            }
            binaries.configureEach { linkTask.enabled = false }

            mavenPublication {
                tasks.withType<AbstractPublishToMaven>()
                    .configureEach { onlyIf { publication != this@mavenPublication } }
                tasks.withType<GenerateModuleMetadata>()
                    .configureEach { onlyIf { publication.get() != this@mavenPublication } }
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
        projectUrl = "https://github.com/Collektive/${rootProject.name}"
        projectLongName = "collektive"
        projectDescription = "DSL for Aggregate Computing in Kotlin"
        licenseName = "Apache License 2.0"
        licenseUrl = "https://opensource.org/license/Apache-2.0/"
        docStyle = DocStyle.HTML
        publishing {
            publications {
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
    configurations.matching { it.name != "detekt" }.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(rootProject.libs.versions.kotlin.get())
            }
            if (requested.group == "org.jetbrains.kotlinx" && requested.name == "kotlinx-coroutines-core") {
                useVersion(rootProject.libs.versions.coroutines.get())
            }
        }
    }

    tasks.withType<Detekt>().configureEach { finalizedBy(reportMerge) }
    reportMerge {
        input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
    }
}

dependencies {
    kover(project(":dsl"))
}

koverReport {
    defaults {
        html { onCheck = true }
        xml { onCheck = true }
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
