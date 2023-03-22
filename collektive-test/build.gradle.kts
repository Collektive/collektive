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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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