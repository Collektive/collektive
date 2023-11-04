plugins {
    `java-gradle-plugin`
    alias(libs.plugins.gradlePluginPublish)
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

buildConfig {
    val project = project(":compiler-plugin")
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.group}.compiler-plugin\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}

// Defines a gradle plugin that can be used from other projects
gradlePlugin {
    plugins {
        website = "https://github.com/Collektive/collektive"
        vcsUrl = "https://github.com/Collektive/collektive.git"
        create("collektive-plugin") {
            id = "it.unibo.collektive.collektive-plugin"
            displayName = "Collektive Plugin"
            description = "This plugin enables the Collektive Kotlin compiler plugin." +
                "It enables the automatic alignment of aggregate operators in the Collektive DSL."
            implementationClass = "it.unibo.collektive.GradlePlugin"
            tags = listOf("kotlin compiler plugin", "aggregate computing", "collektive", "kotlin", "auto-alignment")
        }
    }
}
