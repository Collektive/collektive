plugins {
    application
    kotlin("jvm")
    id("io.github.elisatronetti.kotlinAlignmentPlugin") version "0.1.0"
}

group = "io.github.elisatronetti"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(path=":dsl"))
    implementation("it.unibo.alchemist:alchemist-loading:25.7.2") //abstract class for environment
    implementation("it.unibo.alchemist:alchemist-incarnation-protelis:25.7.2")
    api("it.unibo.alchemist:alchemist-test:25.7.2")
    implementation("it.unibo.alchemist:alchemist-physics:25.7.2")
    implementation("it.unibo.alchemist:alchemist-euclidean-geometry:25.7.2")
    implementation("it.unibo.alchemist:alchemist-implementationbase:25.7.2")
    implementation("it.unibo.alchemist:alchemist-swingui:25.7.2")
    implementation("it.unibo.alchemist:alchemist-loading:25.7.2")
    implementation("it.unibo.alchemist:alchemist-engine:25.7.2") //engine
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