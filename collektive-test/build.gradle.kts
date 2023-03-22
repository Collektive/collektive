@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    kotlin("jvm")
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.collektive.compiler.plugin)
}

group = "io.github.elisatronetti"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(path=":dsl"))
    implementation(libs.bundles.alchemist)
    testImplementation(libs.bundles.jupiter.api)
    testRuntimeOnly(libs.bundles.jupiter.engine)
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}