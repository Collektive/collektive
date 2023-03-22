@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
    val project = project(":compiler-plugin")
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.group}.${project.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}

// Defines a gradle plugin that can be used from other projects
gradlePlugin {
    plugins {
        create("kotlinAlignmentPlugin") {
            id = "io.github.elisatronetti.kotlinAlignmentPlugin"
            displayName = "Kotlin Alignment Plugin"
            description = "Kotlin Alignment Plugin"
            implementationClass = "io.github.elisatronetti.GradlePlugin"
        }
    }
}
