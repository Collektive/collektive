import org.danilopianini.gradle.mavencentral.DocStyle

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.build.config)
    alias(libs.plugins.gitSemVer)
    alias(libs.plugins.gradlePluginPublish)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.multiJvmTesting)
    alias(libs.plugins.publishOnCentral)
    alias(libs.plugins.taskTree)
}

group = "it.unibo.collektive"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

gitSemVer {
    version = computeVersion()
}

buildConfig {
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${project.group}.compiler-plugin\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"compiler-plugin\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}

val pluginName = "collektive-plugin"
val pluginDescription = "Gradle support for the Collektive Kotlin compiler plugin," +
    " performing automatic aggregate alignment of Kotlin sources ."

// Defines a gradle plugin that can be used from other projects
gradlePlugin {
    plugins {
        website = "https://github.com/Collektive/collektive"
        vcsUrl = "https://github.com/Collektive/collektive.git"
        create(pluginName) {
            id = "$group.$pluginName"
            displayName = "Collektive Plugin"
            description = pluginDescription
            implementationClass = "it.unibo.collektive.GradlePlugin"
            tags = listOf("kotlin compiler plugin", "aggregate computing", "collektive", "kotlin", "alignment")
        }
    }
}

signing {
    if (System.getenv("CI") == "true") {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}

publishOnCentral {
    val githubSlug = "Collektive/collektive"
    projectUrl = "https://github.com/$githubSlug"
    projectDescription = pluginDescription
    licenseName = "Apache License 2.0"
    projectLongName = "Collektive kotlin compiler plugin"
    licenseUrl = "https://opensource.org/license/Apache-2.0/"
    docStyle = DocStyle.HTML
    publishing {
        publications {
            withType<MavenPublication>().configureEach {
                if ("OSSRH" !in name) {
                    artifact(tasks.javadocJar)
                }
                scmConnection = "git:git@github.com:$githubSlug"
                projectUrl = "https://github.com/$githubSlug"
                pom {
                    developers {
                        developer {
                            name = "Elisa Tronetti"
                            email = "elisa.tronetti@studio.unibo.it"
                            url = "https://github.com/ElisaTronetti"
                        }
                        developer {
                            name = "Nicolas Farabegoli"
                            email = "nicolas.farabegoli@unibo.it"
                            url = "https://nicolasfarabegoli.it"
                        }
                        developer {
                            name = "Danilo Pianini"
                            email = "danilo.pianin@unibo.it"
                            url = "https://danysk.github.io"
                        }
                    }
                }
            }
        }
    }
}
