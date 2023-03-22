@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
}

group = "io.github.elisatronetti"
version = "0.1.0"

repositories {
    mavenCentral()
}
