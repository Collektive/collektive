plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
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

gradlePlugin {
    plugins {
        create("kotlinIrPlugin") {
            id = "com.etronetti.kotlinIrPlugin"
            displayName = "Kotlin Ir Plugin"
            description = "Kotlin Ir Plugin"
            implementationClass = "com.etronetti.GradlePlugin"
        }
    }
}