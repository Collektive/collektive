import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
}

apply(plugin = libs.plugins.kotlin.jvm.id)

val targetJvm = JvmTarget.JVM_1_8

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJvm.target.substringAfterLast('.').toInt()))
    }
}

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation("it.unibo.collektive:compiler-plugin")
                implementation(libs.bundles.kotlin.compiler)
                implementation(libs.slf4j)
            }
        }
        val test by getting {
            dependencies {
                implementation(project(":dsl"))
                implementation(project(":compiler-embeddable"))
                implementation(rootProject.libs.kotest.runner.junit5.jvm)
                implementation(libs.javap)
                implementation(libs.kotlinpoet)
                implementation(libs.subjekt)
            }
        }
    }
    compilerOptions {
        jvmTarget.set(targetJvm)
    }
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            events =
                setOf(
                    TestLogEvent.FAILED,
                    TestLogEvent.PASSED,
                )
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

tasks {
    // Prevent publishing this module since it is a test-only module
    withType<AbstractPublishToMaven>().configureEach {
        enabled = false
    }
    withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }
}
