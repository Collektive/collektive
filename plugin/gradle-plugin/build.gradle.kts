plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.gradlePluginPublish)
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
        website = "https://github.com/Collektive/collektive"
        vcsUrl = "https://github.com/Collektive/collektive.git"
        create("kotlinAlignmentPlugin") {
            id = "it.unibo.collektive.kotlinAlignmentPlugin"
            displayName = "Kotlin Alignment Plugin"
            description = "Kotlin Alignment Plugin"
            implementationClass = "it.unibo.collektive.GradlePlugin"
            tags = listOf("compiler-plugin", "aggregate-computing", "collektive")
        }
    }
}
