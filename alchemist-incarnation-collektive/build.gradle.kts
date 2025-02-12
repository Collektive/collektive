import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
}

apply(plugin = rootProject.libs.plugins.kotlin.jvm.id)

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
                implementation("it.unibo.collektive:compiler-plugin")
                implementation(project(":compiler-embeddable"))
                implementation(project(":dsl"))
                implementation(project(":stdlib"))
                implementation(kotlin("reflect"))
                implementation(libs.bundles.alchemist)
                implementation(libs.bundles.kotlin.compiler)
                implementation(libs.caffeine)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.serialization)
                implementation(libs.slf4j)
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
