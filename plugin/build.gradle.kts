plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
    alias(libs.plugins.gitSemVer)
}

group = "it.unibo.collektive"

repositories {
    mavenCentral()
}