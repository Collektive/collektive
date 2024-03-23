import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
}

apply(plugin = libs.plugins.kotlin.jvm.id)

val targetJvm = JvmTarget.JVM_17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJvm.target.toInt()))
    }
}

kotlinJvm {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation(libs.kotlinpoet)
                implementation(project(":dsl"))
            }
        }
        val test by getting {
            dependencies {
                implementation(libs.kotlin.compile.testing)
                implementation(libs.kotest.runner.junit5.jvm)
                implementation(project(":compiler-embeddable"))
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
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
            )
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}
