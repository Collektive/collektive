plugins {
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}